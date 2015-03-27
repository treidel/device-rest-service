package com.fancypants.stream.exception;

public class StreamException extends Exception {

	private static final long serialVersionUID = -8610629334980375760L;

	public StreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public StreamException(String message) {
		super(message);
	}

	public StreamException(Throwable cause) {
		super(cause);
	}

}
