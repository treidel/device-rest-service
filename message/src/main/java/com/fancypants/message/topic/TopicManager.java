package com.fancypants.message.topic;

import com.fancypants.message.exception.AbstractMessageException;

public interface TopicManager {

	void topicCreate(String topic) throws AbstractMessageException;

	void topicDestroy(String topic) throws AbstractMessageException;

	TopicProducer topicProducer(String topic) throws AbstractMessageException;

	TopicConsumer topicConsumer(String topic, TopicConsumer.Handler handler) throws AbstractMessageException;

}
