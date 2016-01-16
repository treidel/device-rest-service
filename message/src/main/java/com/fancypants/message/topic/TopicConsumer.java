package com.fancypants.message.topic;

import com.fancypants.message.exception.AbstractMessageException;

public interface TopicConsumer {

	void start() throws AbstractMessageException;

	void close();

	public interface Handler {
		void handle(String message);
	}
}
