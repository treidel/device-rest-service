package com.fancypants.storm.usage.config;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.DataScanMe;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.message.MessageScanMe;
import com.fancypants.storm.StormScanMe;
import com.fancypants.storm.config.AbstractTopologyConfig;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionTupleMapper;
import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;
import com.fancypants.storm.usage.StormUsageScanMe;
import com.fancypants.storm.usage.aggregate.HourlyEnergyCalculationAggregator;
import com.fancypants.storm.usage.aggregate.UsageAggregator;
import com.fancypants.storm.usage.state.TopicNotifierStateFactory;
import com.fancypants.storm.usage.state.TopicNotifierStateUpdater;
import com.fancypants.storm.usage.state.UsageStateFactory;
import com.fancypants.storm.usage.state.UsageStateUpdater;
import com.fancypants.usage.UsageScanMe;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.fluent.GroupedStream;
import storm.trident.spout.IOpaquePartitionedTridentSpout;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataScanMe.class, DeviceScanMe.class, UsageScanMe.class,
		MessageScanMe.class, StormScanMe.class, StormUsageScanMe.class })
public class UsageConfig extends AbstractTopologyConfig {
	private final static Logger LOG = LoggerFactory.getLogger(UsageConfig.class);

	private final static String TRIDENT_TOPOLOGY = "filtered_records_topology";
	private final static String FILTERED_RECORDS_TXID = "filtered_records";

	@Autowired
	private UsageStateFactory usageStateFactory;

	@Autowired
	private TopicNotifierStateFactory topicNotifierStateFactory;

	@Autowired
	private UsageStateUpdater usageStateUpdater;

	@Autowired
	private TopicNotifierStateUpdater topicNotifierUpdater;

	@Autowired
	private HourlyEnergyCalculationAggregator hourlyEnergyCalculationAggregator;

	@Autowired
	private UsageAggregator usageAggregator;

	@SuppressWarnings("rawtypes")
	@Autowired
	@Qualifier(AbstractTopologyConfig.TRIDENT_SPOUT_NAME)
	private Pair<Config, IOpaquePartitionedTridentSpout> spout;

	@Bean
	public Pair<Config, StormTopology> usageTopology() {
		LOG.trace("usageTopology enter");

		// create the trident topology
		TridentTopology tridentTopology = new TridentTopology();

		// create the config
		Config config = new Config();

		// add the spout config
		config.putAll(spout.getKey());

		// setup the filtered stream
		Stream filteredStream = tridentTopology.newStream(FILTERED_RECORDS_TXID, spout.getValue());
		Stream node1 = filteredStream.partitionBy(new Fields(RawRecordEntity.DEVICE_ATTRIBUTE));
		Stream node2 = node1.aggregate(RawRecordTupleMapper.getOutputFields(), hourlyEnergyCalculationAggregator,
				EnergyConsumptionTupleMapper.getOutputFields());
		GroupedStream node3 = node2.groupBy(new Fields(EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE,
				EnergyConsumptionRecordEntity.DATE_ATTRIBUTE));
		Stream node4 = node3.aggregate(new Fields(EnergyConsumptionEntityMapper.ATTRIBUTES), usageAggregator,
				new Fields("result"));
		node4.partitionPersist(usageStateFactory, new Fields("result"), usageStateUpdater);
		node4.partitionPersist(topicNotifierStateFactory, new Fields("result"), topicNotifierUpdater);
		StormTopology stormTopology = tridentTopology.build();
		Pair<Config, StormTopology> pair = new ImmutablePair<Config, StormTopology>(new Config(), stormTopology);
		LOG.trace("usageTopology exit {}", pair);
		return pair;
	}

	@Override
	protected Pair<Config, StormTopology> getTopology() {
		return usageTopology();
	}

	@Override
	protected String getTopologyPrefix() {
		return TRIDENT_TOPOLOGY;
	}
}
