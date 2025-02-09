package com.ambev.ms_order.exception;

public class OrderAlreadExistsException extends RuntimeException {
	
    private static final long serialVersionUID = -2270408349299592723L;

	public OrderAlreadExistsException(String message) {
        super(message);
    }
}