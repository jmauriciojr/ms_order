package com.ambev.ms_order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ambev.ms_order.commons.enums.OrderStatus;
import com.ambev.ms_order.commons.mapper.OrderMapper;
import com.ambev.ms_order.mock.MockUtil;
import com.ambev.ms_order.model.Order;
import com.ambev.ms_order.model.dto.OrderRequestDTO;
import com.ambev.ms_order.repository.OrderRepository;
import com.ambev.ms_order.service.impl.OrderAsyncServiceImpl;

class OrderAsyncServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private RedisLockService redisLockService;

    @InjectMocks
    private OrderAsyncServiceImpl orderAsyncServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processOrderAsync_shouldProcessOrderSuccessfully() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setExternalId("123");
        Order order = MockUtil.createMockOrder();
        order.setItems(MockUtil.createMockOrderItemList());
        
        when(orderMapper.requestToEntity(orderRequestDTO)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderAsyncServiceImpl.processOrderAsync(orderRequestDTO, "lockKey");

        verify(orderRepository, times(3)).save(order);
        verify(redisLockService).releaseLock("lockKey");
        
        assertEquals(OrderStatus.CALCULATED, order.getStatus());
    }

    @Test
    void processOrderAsync_shouldHandleExceptionWhenCalculatingTotal() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setExternalId("123");
        Order order = MockUtil.createMockOrder();
        
        when(orderMapper.requestToEntity(orderRequestDTO)).thenReturn(order);

        assertThrows(RuntimeException.class, () -> {
            orderAsyncServiceImpl.processOrderAsync(orderRequestDTO, "lockKey");
        });

        verify(redisLockService).releaseLock("lockKey");
        assertEquals(OrderStatus.ERROR, order.getStatus());
    }

    @Test
    void calculateTotal_shouldCalculateTotalSuccessfully() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setExternalId("123");
        Order order = MockUtil.createMockOrder();
        order.setItems(MockUtil.createMockOrderItemList());
        
        when(orderMapper.requestToEntity(orderRequestDTO)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderAsyncServiceImpl.processOrderAsync(orderRequestDTO, "lockKey");

        assertEquals(new BigDecimal(20), order.getTotalValue());
        assertEquals(OrderStatus.CALCULATED, order.getStatus());
    }

}
