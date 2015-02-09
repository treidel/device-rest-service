package com.fancypants.storm.processing.device.record.state;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TopicNotifierStateUpdater extends
		BaseStateUpdater<TopicNotifierState> {

	private static final Logger LOG = LoggerFactory
			.getLogger(TopicNotifierStateUpdater.class);
	private static final long serialVersionUID = 8383124026395269331L;

	@Autowired
	private ObjectMapper mapper;

	@Override
	public void updateState(TopicNotifierState state,
			List<TridentTuple> tuples, TridentCollector collector) {
		LOG.trace("TopicNotifierStateUpdater.updateState enter", "state",
				state, "tuples", tuples, "collector", collector);

		try {
			// iterate through each tuple
			for (TridentTuple tuple : tuples) {
				// get the record
				EnergyConsumptionRecordEntity record = (EnergyConsumptionRecordEntity) tuple
						.get(0);
				// serialize the record
				String json = mapper.writeValueAsString(record);
				// create the pair
				Pair<String, String> pair = new ImmutablePair<String, String>(
						record.getDevice(), json);
				// do the write
				state.update(pair);
			}
		} catch (JsonProcessingException e) {
			LOG.error("error serializing", e);
			throw new IllegalStateException(e);
		}
		LOG.trace("TopicNotifierStateUpdater.updateState exit");
	}
}
