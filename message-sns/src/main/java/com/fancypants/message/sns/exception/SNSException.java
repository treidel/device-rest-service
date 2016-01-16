package com.fancypants.message.sns.exception;

import com.fancypants.message.exception.AbstractMessageException;

public class SNSException extends AbstractMessageException {
	private static final long serialVersionUID = -7079227597256081822L;
	

	public SNSException(String message) {
		super(message);
	}

	public SNSException(Throwable reason) {
		super(reason);
	}
}
