package com.fancypants.message;

public abstract class AbstractMessageException extends Exception {

	private static final long serialVersionUID = -1423058202722069966L;

	protected AbstractMessageException(String message) {
		super(message);
	}
	
	protected AbstractMessageException(Throwable reason) {
		super(reason);
	}
}
