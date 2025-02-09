package com.ambev.ms_order.service;

import com.ambev.ms_order.model.dto.OrderRequestDTO;

public interface OrderAsyncService {

	void processOrderAsync(OrderRequestDTO order, String lockKey) ;
	
}
