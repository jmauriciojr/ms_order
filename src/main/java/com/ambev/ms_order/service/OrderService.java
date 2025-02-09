package com.ambev.ms_order.service;

import java.util.List;

import com.ambev.ms_order.model.dto.OrderDTO;
import com.ambev.ms_order.model.dto.OrderRequestDTO;

public interface OrderService {

	void processOrder(OrderRequestDTO order) ;
	
	OrderDTO getOrderByExternalId(String externalId);
	
	List<OrderDTO> getAllOrders();
}
