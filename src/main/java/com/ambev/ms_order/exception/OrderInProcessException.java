package com.ambev.ms_order.exception;

public class OrderInProcessException extends RuntimeException {
	
    private static final long serialVersionUID = -2270408349299592723L;

	public OrderInProcessException(String message) {
        super(message);
    }
}