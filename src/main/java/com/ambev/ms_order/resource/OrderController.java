package com.ambev.ms_order.resource;

import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_DATA;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_EXTERNAL_ORDER_ID;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_MESSAGE;
import static com.ambev.ms_order.commons.constants.Constants.LOG_KEY_METHOD;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambev.ms_order.exception.ErrorResponse;
import com.ambev.ms_order.model.dto.OrderDTO;
import com.ambev.ms_order.model.dto.OrderRequestDTO;
import com.ambev.ms_order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@Operation(summary = "Process Order for calculation of the total amount")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "202", description = "Order received and processing started."),
			@ApiResponse(responseCode = "400", description = "Invalid request!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "404", description = "Order Not Found!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "500", description = "Unexpected Error!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@PostMapping("/calculate")
	public ResponseEntity<String> calculateOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Request received to process order", "processOrder", 
				LOG_KEY_EXTERNAL_ORDER_ID + orderRequestDTO.getExternalId());
		
		orderService.processOrder(orderRequestDTO);

        return new ResponseEntity<>("Order processing initiated.", HttpStatus.ACCEPTED);
    }

	@Operation(summary = "Get all orders")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Orders retrieved successfully."),
			@ApiResponse(responseCode = "500", description = "Unexpected Error!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OrderDTO>> getOrders() {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Request recevived to get all orders", "getOrders");
		
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	@Operation(summary = "Get order by orderExternalID")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Order retrieved successfully."),
			@ApiResponse(responseCode = "500", description = "Unexpected Error!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@GetMapping(value = "/{orderExternalId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderDTO> getOrderByExternalId(
			@Parameter(description = "Order External ID [*Required]", required = true) 
			@PathVariable("orderExternalId") @NotEmpty String orderExternalId) {
		
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_DATA, "Request recevived to get order by orderExternalID",
				"getOrderByExternalId", LOG_KEY_EXTERNAL_ORDER_ID + orderExternalId);

		return ResponseEntity.ok(orderService.getOrderByExternalId(orderExternalId));
	}
}
