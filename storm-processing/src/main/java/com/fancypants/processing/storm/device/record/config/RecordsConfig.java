package com.fancypants.processing.storm.device.record.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.fluent.GroupedStream;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.IRichSpout;
import backtype.storm.tuple.Fields;

import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.processing.storm.device.record.aggregate.HourlyEnergyCalculationAggregator;
import com.fancypants.processing.storm.device.record.aggregate.UsageAggregator;
import com.fancypants.processing.storm.device.record.filter.PrintFilter;
import com.fancypants.processing.storm.device.record.state.TopicNotifierStateFactory;
import com.fancypants.processing.storm.device.record.state.TopicNotifierStateUpdater;
import com.fancypants.processing.storm.device.record.state.UsageStateFactory;
import com.fancypants.processing.storm.device.record.state.UsageStateUpdater;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionTupleMapper;
import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;

@Configuration
public class RecordsConfig {

	private final static String TOPOLOGY = "raw_records";
	private final static String TXID = "records";

	@Autowired 
	private HourlyRecordRepository hourlyRepository;

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

	@Autowired
	private PrintFilter printFilter;

	@Autowired
	private IRichSpout spout;

	@PostConstruct
	public void init() throws Exception {

		// create the topology
		TridentTopology topology = new TridentTopology();

		// setup the stream
		Stream stream = topology.newStream(TXID, spout);
		Stream node1 = stream.partitionBy(new Fields(
				RawRecordEntity.DEVICE_ATTRIBUTE));
		node1.each(RawRecordTupleMapper.getOutputFields(), printFilter);
		Stream node2 = node1.aggregate(RawRecordTupleMapper.getOutputFields(),
				hourlyEnergyCalculationAggregator,
				EnergyConsumptionTupleMapper.getOutputFields());
		node2.each(EnergyConsumptionTupleMapper.getOutputFields(),
				new PrintFilter());
		GroupedStream node3 = node2.groupBy(new Fields(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE,
				EnergyConsumptionRecordEntity.DATE_ATTRIBUTE));
		Stream node4 = node3.aggregate(new Fields(
				EnergyConsumptionEntityMapper.ATTRIBUTES), usageAggregator,
				new Fields("result"));
		node4.each(new Fields("result"), new PrintFilter());
		node4.partitionPersist(usageStateFactory, new Fields("result"),
				new UsageStateUpdater());
		node4.partitionPersist(topicNotifierStateFactory, new Fields("result"),
				new TopicNotifierStateUpdater());

		// create the config
		Config conf = new Config();
		// for now create a local cluster
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY, conf, topology.build());
	}
}
