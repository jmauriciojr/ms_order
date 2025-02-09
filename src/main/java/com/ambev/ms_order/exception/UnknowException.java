package com.ambev.ms_order.exception;

public class UnknowException extends RuntimeException {
	
    private static final long serialVersionUID = -2270408349299592723L;

	public UnknowException(String message) {
        super(message);
    }
}