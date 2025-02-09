package com.ambev.ms_order.exception;

public class OrderBadRequestException extends RuntimeException {
	
    private static final long serialVersionUID = -2270408349299592723L;

	public OrderBadRequestException(String message) {
        super(message);
    }
}