package com.ambev.ms_order.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ambev.ms_order.commons.enums.OrderStatus;
import com.ambev.ms_order.commons.mapper.OrderMapper;
import com.ambev.ms_order.exception.OrderAlreadExistsException;
import com.ambev.ms_order.exception.OrderBadRequestException;
import com.ambev.ms_order.exception.OrderNotFoundException;
import com.ambev.ms_order.mock.MockUtil;
import com.ambev.ms_order.model.Order;
import com.ambev.ms_order.model.dto.OrderDTO;
import com.ambev.ms_order.model.dto.OrderRequestDTO;
import com.ambev.ms_order.repository.OrderRepository;
import com.ambev.ms_order.service.impl.OrderServiceImpl;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderAsyncService orderAsyncService;

    @Mock
    private RedisLockService redisLockService;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processOrder_withValidRequest_processesOrder() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setExternalId("validId");

        when(orderRepository.findByExternalId(orderRequestDTO.getExternalId())).thenReturn(Optional.empty());
        when(redisLockService.createLockKey(orderRequestDTO.getExternalId())).thenReturn("lockKey");

        orderServiceImpl.processOrder(orderRequestDTO);

        verify(orderAsyncService).processOrderAsync(orderRequestDTO, "lockKey");
    }

    @Test
    void processOrder_withExistingOrder_throwsOrderAlreadExistsException() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setExternalId("existingId");

        when(orderRepository.findByExternalId(orderRequestDTO.getExternalId())).thenReturn(Optional.of(new Order()));

        assertThrows(OrderAlreadExistsException.class, () -> orderServiceImpl.processOrder(orderRequestDTO));
    }

    @Test
    void getOrderByExternalId_withValidId_returnsOrder() {
        String externalId = "validId";
		Order order = MockUtil.createMockOrder();

        when(orderRepository.findByExternalId(externalId)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(MockUtil.createMockOrderDTO(OrderStatus.CALCULATED));

        orderServiceImpl.getOrderByExternalId(externalId);

        verify(orderRepository).findByExternalId(externalId);
        verify(orderMapper).toDTO(order);
    }

    @Test
    void getOrderByExternalId_withInvalidId_throwsOrderNotFoundException() {
        String externalId = "invalidId";

        when(orderRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderServiceImpl.getOrderByExternalId(externalId));
    }

    @Test
    void getOrderByExternalId_withEmptyId_throwsOrderBadRequestException() {
        String externalId = "";

        assertThrows(OrderBadRequestException.class, () -> orderServiceImpl.getOrderByExternalId(externalId));
    }

    @Test
    void getOrderByExternalId_withNullId_throwsOrderBadRequestException() {
        String externalId = null;

        assertThrows(OrderBadRequestException.class, () -> orderServiceImpl.getOrderByExternalId(externalId));
    }
    
    @Test
    void getAllOrders_whenOrdersExist_returnsOrdersList() {
        when(orderRepository.findAll()).thenReturn(MockUtil.createMockOrderList());
        when(orderMapper.toListDTO(any())).thenReturn(MockUtil.createMockOrderDTOList());

        List<OrderDTO> result = orderServiceImpl.getAllOrders();

        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getExternalId());
    }

    @Test
    void getAllOrders_whenNoOrdersExist_returnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        when(orderMapper.toListDTO(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<OrderDTO> result = orderServiceImpl.getAllOrders();

        assertTrue(result.isEmpty());
    }
    
}