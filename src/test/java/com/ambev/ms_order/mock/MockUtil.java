package com.ambev.ms_order.mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.ambev.ms_order.commons.enums.OrderStatus;
import com.ambev.ms_order.model.Order;
import com.ambev.ms_order.model.OrderItem;
import com.ambev.ms_order.model.dto.OrderDTO;
import com.ambev.ms_order.model.dto.OrderItemDTO;
import com.ambev.ms_order.model.dto.OrderItemRequestDTO;
import com.ambev.ms_order.model.dto.OrderRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockUtil {
	
	
	public static Order createMockOrder() {
		Order order = new Order();
		order.setExternalId("123");
		
		return order;
	}
	
	public static List<Order> createMockOrderList() {
		return List.of(createMockOrder());
	}

	public static OrderItem createMockOrderItem(BigDecimal unitPrice, int quantity) {
		OrderItem item = new OrderItem();
		item.setUnitPrice(unitPrice);
		item.setQuantity(quantity);
		return item;
	}

	public static List<OrderItem> createMockOrderItemList() {
		return Collections.singletonList(createMockOrderItem(BigDecimal.TEN, 2));
	}
	
	
	public static OrderDTO createMockOrderDTO(OrderStatus status) {
		OrderDTO orderDto = new OrderDTO();
		orderDto.setExternalId("123");
		orderDto.setStatus(status);
		orderDto.setItems(createMockOrderItemDTOList());
		
		return orderDto;
	}
	
	public static List<OrderDTO> createMockOrderDTOList() {
		return List.of(createMockOrderDTO(OrderStatus.CALCULATED));
	}
	
	public static OrderItemDTO createMockOrderItemDTO(BigDecimal unitPrice, int quantity) {
		OrderItemDTO item = new OrderItemDTO();
		item.setUnitPrice(unitPrice);
		item.setQuantity(quantity);
		return item;
	}
	
	public static List<OrderItemDTO> createMockOrderItemDTOList() {
		return Collections.singletonList(createMockOrderItemDTO(BigDecimal.TEN, 2));
	}

	
	public static OrderRequestDTO createMockOrderRequestDTO() {
		OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
		orderRequestDTO.setExternalId("123");
		orderRequestDTO.setItems(createMockOrderItemRequestDTOList());

		return orderRequestDTO;
	}
	
	public static OrderItemRequestDTO createMockOrderItemRequestDTO(BigDecimal unitPrice, int quantity) {
		OrderItemRequestDTO item = new OrderItemRequestDTO();
		item.setUnitPrice(unitPrice);
		item.setQuantity(quantity);
		return item;
	}
	
	public static List<OrderItemRequestDTO> createMockOrderItemRequestDTOList() {
		return Collections.singletonList(createMockOrderItemRequestDTO(BigDecimal.TEN, 2));
	}
	
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
