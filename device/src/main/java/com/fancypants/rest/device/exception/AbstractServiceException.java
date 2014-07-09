package com.fancypants.rest.device.exception;

public abstract class AbstractServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8801994631886112402L;

	protected AbstractServiceException(String message) {
		super(message);
	}
	
}
