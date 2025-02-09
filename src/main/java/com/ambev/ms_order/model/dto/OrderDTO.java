package com.ambev.ms_order.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.ambev.ms_order.commons.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {

	private static final long serialVersionUID = 2531730707193552250L;

	private Long id;
	private String externalId;
	private BigDecimal totalValue;
	private OrderStatus status;
	private List<OrderItemDTO> items;

}
