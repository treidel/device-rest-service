package com.fancypants.storm.processing.device.record.state;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.state.State;
import storm.trident.state.ValueUpdater;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;

public class TopicNotifierState implements State,
		ValueUpdater<Pair<String, String>> {

	private static final Logger LOG = LoggerFactory
			.getLogger(TopicNotifierState.class);

	private final TopicManager topicManager;

	public TopicNotifierState(TopicManager topicManager) {
		LOG.trace("TopicNotifierState.TopicNotifierState enter topicManager="
				+ topicManager);
		this.topicManager = topicManager;
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
	public Pair<String, String> update(Pair<String, String> stored) {
		LOG.trace("TopicNotifierState.update enter stored=" + stored);
		try {
			// create the producer
			TopicProducer producer = topicManager
					.topicProducer(stored.getKey());
			// send out the notification
			producer.sendMessage(stored.getValue());
		} catch (AbstractMessageException e) {
			LOG.error("unable to send notification", e);
		}
		// don't emit anything
		LOG.trace("TopicNotifierState.update exit");
		return null;
	}

}
