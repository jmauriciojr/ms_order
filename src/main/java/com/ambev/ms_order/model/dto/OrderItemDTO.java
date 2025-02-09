package com.ambev.ms_order.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO implements Serializable {

	private static final long serialVersionUID = 7582250525816816160L;
	
	private Long id;
	private String name;
	private BigDecimal unitPrice;
	private Integer quantity;

}
