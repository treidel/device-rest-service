package com.fancypants.message.topic;

import com.fancypants.message.exception.AbstractMessageException;

public interface TopicConsumer {

	void receiveMessages(Handler handler) throws AbstractMessageException;

	void close();
	
	interface Handler {
		void handle(String message);
	}
}
