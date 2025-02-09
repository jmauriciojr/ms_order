package com.ambev.ms_order.service.impl;

import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_DATA;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_EXTERNAL_ORDER_ID;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_MESSAGE;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_METHOD;

import java.math.BigDecimal;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambev.ms_order.commons.enums.OrderStatus;
import com.ambev.ms_order.commons.mapper.OrderMapper;
import com.ambev.ms_order.exception.UnknowException;
import com.ambev.ms_order.model.Order;
import com.ambev.ms_order.model.dto.OrderRequestDTO;
import com.ambev.ms_order.repository.OrderRepository;
import com.ambev.ms_order.service.OrderAsyncService;
import com.ambev.ms_order.service.RedisLockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAsyncServiceImpl implements OrderAsyncService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
    private final RedisLockService redisLockService;
    
    
	@Override
	@Async
	@Transactional
	public void processOrderAsync(OrderRequestDTO orderRequestDTO, String lockKey) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Asynchronous process order", "processOrderAsync", 
				LOG_KEY_EXTERNAL_ORDER_ID + orderRequestDTO.getExternalId());
		
		Order order = orderMapper.requestToEntity(orderRequestDTO);
		try {
			order.setStatus(OrderStatus.RECEIVED);
			order.setTotalValue(BigDecimal.ZERO);
			order.getItems().forEach(item -> item.setOrder(order));

			Order savedOrder = orderRepository.save(order);
			this.calculateTotal(savedOrder);

		} catch (Exception e) {
			log.error(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Error when process the order total",
					"calculateTotal", LOG_KEY_EXTERNAL_ORDER_ID + order.getExternalId());
			order.setStatus(OrderStatus.ERROR);
			orderRepository.save(order);
			
			throw new UnknowException("Error when process the order total: " + order.getExternalId());
		} finally {
			redisLockService.releaseLock(lockKey);
		}

	}
	
	private void calculateTotal(Order order) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Calculate order total", "calculateTotal", 
				LOG_KEY_EXTERNAL_ORDER_ID + order.getExternalId());

			order.setStatus(OrderStatus.PROCESSING);
			orderRepository.save(order);
			
			BigDecimal total = order.getItems().stream()
					.map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			order.setTotalValue(total);
			order.setStatus(OrderStatus.CALCULATED);

			orderRepository.save(order);
			
	}
		
}