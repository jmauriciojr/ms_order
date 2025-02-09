package com.ambev.ms_order.commons.mapper;

import java.time.ZoneOffset;
import java.util.List;

import org.mapstruct.Mapper;

import com.ambev.ms_order.model.Order;
import com.ambev.ms_order.model.dto.OrderDTO;
import com.ambev.ms_order.model.dto.OrderRequestDTO;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;

    
	OrderDTO toDTO(Order order);

	Order toEntity(OrderDTO orderDTO);

	List<OrderDTO> toListDTO(List<Order> listEntity);
	
	Order requestToEntity(OrderRequestDTO orderRequestDTO);


}
