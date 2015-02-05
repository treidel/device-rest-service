package com.fancypants.test.message.exception;

import com.fancypants.message.exception.AbstractMessageException;

public class TestMessageException extends AbstractMessageException {
	private static final long serialVersionUID = -7079227597256081822L;
	

	public TestMessageException(String message) {
		super(message);
	}

	public TestMessageException(Throwable reason) {
		super(reason);
	}
}
