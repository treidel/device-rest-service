package com.fancypants.processing.storm.device.record.state;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

import com.fancypants.message.topic.TopicProducer;

@Component
public class TopicNotifierStateFactory implements StateFactory {

	private static final long serialVersionUID = -8884066339861949756L;

	@Autowired
	private TopicProducer producer;

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		// create the backing map
		TopicNotifierState state = new TopicNotifierState(producer);
		return state;
	}

}
