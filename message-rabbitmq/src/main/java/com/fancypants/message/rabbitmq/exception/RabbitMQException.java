package com.fancypants.message.rabbitmq.exception;

import com.fancypants.message.exception.AbstractMessageException;

public class RabbitMQException extends AbstractMessageException {
	private static final long serialVersionUID = -7079227597256081822L;
	

	public RabbitMQException(String message) {
		super(message);
	}

	public RabbitMQException(Throwable reason) {
		super(reason);
	}
}
