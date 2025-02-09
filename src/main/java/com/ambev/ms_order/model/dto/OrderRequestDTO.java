package com.ambev.ms_order.model.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO implements Serializable {

	private static final long serialVersionUID = 2531730707193552250L;

	@NotEmpty(message = "ExternalId is required")
	private String externalId;
	
	@NotEmpty(message = "Items is required")
	private List<OrderItemRequestDTO> items;
}
