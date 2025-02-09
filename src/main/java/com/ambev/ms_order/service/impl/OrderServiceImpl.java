package com.ambev.ms_order.service.impl;

import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_DATA;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_EXTERNAL_ORDER_ID;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_MESSAGE;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_METHOD;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ambev.ms_order.commons.mapper.OrderMapper;
import com.ambev.ms_order.exception.OrderAlreadExistsException;
import com.ambev.ms_order.exception.OrderBadRequestException;
import com.ambev.ms_order.exception.OrderNotFoundException;
import com.ambev.ms_order.model.Order;
import com.ambev.ms_order.model.dto.OrderDTO;
import com.ambev.ms_order.model.dto.OrderRequestDTO;
import com.ambev.ms_order.repository.OrderRepository;
import com.ambev.ms_order.service.OrderAsyncService;
import com.ambev.ms_order.service.OrderService;
import com.ambev.ms_order.service.RedisLockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderAsyncService orderAsyncService;
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
    private final RedisLockService redisLockService;

    @Override
    public void processOrder(OrderRequestDTO orderRequestDTO) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Process order", "processOrder", 
				LOG_KEY_EXTERNAL_ORDER_ID + orderRequestDTO.getExternalId());

		this.validateOrder(orderRequestDTO.getExternalId());
		
		String lockKey = redisLockService.createLockKey(orderRequestDTO.getExternalId());
	      
		redisLockService.validateLock(lockKey);	
			
		orderAsyncService.processOrderAsync(orderRequestDTO, lockKey);
	}

	@Override
    public OrderDTO getOrderByExternalId(String externalId) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Retrieve order by external id", "getOrderByExternalId",
				LOG_KEY_EXTERNAL_ORDER_ID + externalId);
		
		validateOrderExternalId(externalId);
		
		Order order = orderRepository.findByExternalId(externalId)
               .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado com externalId: " + externalId));
		
		return orderMapper.toDTO(order);
    }
    

	@Override
    public List<OrderDTO> getAllOrders() {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD, "Retrieve all orders", "getAllOrders");
		
        return orderMapper.toListDTO(orderRepository.findAll());
    }
    
	private void validateOrder(String orderExternalId) {
		Optional<Order> existing = orderRepository.findByExternalId(orderExternalId);
		if (existing.isPresent()) {
			throw new OrderAlreadExistsException("Order already exists with externalId: " + orderExternalId);
		}
	}
	
	private void validateOrderExternalId(String orderExternalId) {
		if (orderExternalId == null || orderExternalId.isBlank()) {
			throw new OrderBadRequestException("Order External ID is Required");
		}
	}

	
}
