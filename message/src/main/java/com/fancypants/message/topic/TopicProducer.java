package com.fancypants.message.topic;

import com.fancypants.message.AbstractMessageException;

public interface TopicProducer {

	void sendMessage(String message) throws AbstractMessageException;

	void close();
}
