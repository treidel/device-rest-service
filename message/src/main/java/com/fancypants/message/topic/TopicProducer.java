package com.fancypants.message.topic;

import com.fancypants.message.exception.AbstractMessageException;

public interface TopicProducer {

	void sendMessage(String message) throws AbstractMessageException;

	void close();
}
