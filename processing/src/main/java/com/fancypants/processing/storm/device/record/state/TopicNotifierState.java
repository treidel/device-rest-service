package com.fancypants.processing.storm.device.record.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.state.State;
import storm.trident.state.ValueUpdater;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicProducer;

public class TopicNotifierState implements State, ValueUpdater<String> {

	private static final Logger LOG = LoggerFactory
			.getLogger(TopicNotifierState.class);

	private final TopicProducer producer;

	public TopicNotifierState(TopicProducer producer) {
		LOG.trace("TopicNotifierState.TopicNotifierState enter producer="
				+ producer);
		this.producer = producer;
		LOG.trace("TopicNotifierState.TopicNotifierState exit");
	}

	@Override
	public void beginCommit(Long txid) {
		LOG.trace("TopicNotifierState.beginCommit enter txid=" + txid);
		LOG.trace("TopicNotifierState.beginCommit exit");
	}

	@Override
	public void commit(Long txid) {
		LOG.trace("TopicNotifierState.commit enter txid=" + txid);
		LOG.trace("TopicNotifierState.commit exit");
	}

	@Override
	public String update(String stored) {
		LOG.trace("TopicNotifierState.update enter stored=" + stored);
		try {
			// send out the notification
			producer.sendMessage(stored);
		} catch (AbstractMessageException e) {
			LOG.error("unable to send notification", e);
		}
		// don't emit anything
		LOG.trace("TopicNotifierState.update exit");
		return null;
	}

}
