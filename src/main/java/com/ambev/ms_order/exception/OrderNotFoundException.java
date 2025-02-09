package com.ambev.ms_order.exception;

public class OrderNotFoundException extends RuntimeException {
	
    private static final long serialVersionUID = -2270408349299592723L;

	public OrderNotFoundException(String message) {
        super(message);
    }
}