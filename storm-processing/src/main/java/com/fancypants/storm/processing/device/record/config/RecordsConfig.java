package com.fancypants.storm.processing.device.record.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.fluent.GroupedStream;
import storm.trident.spout.ITridentSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.DataScanMe;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.repository.HourlyRecordRepository;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.message.MessageScanMe;
import com.fancypants.storm.StormScanMe;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionTupleMapper;
import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;
import com.fancypants.storm.processing.StormProcessingScanMe;
import com.fancypants.storm.processing.device.record.aggregate.HourlyEnergyCalculationAggregator;
import com.fancypants.storm.processing.device.record.aggregate.UsageAggregator;
import com.fancypants.storm.processing.device.record.filter.PrintFilter;
import com.fancypants.storm.processing.device.record.state.TopicNotifierStateFactory;
import com.fancypants.storm.processing.device.record.state.TopicNotifierStateUpdater;
import com.fancypants.storm.processing.device.record.state.UsageStateFactory;
import com.fancypants.storm.processing.device.record.state.UsageStateUpdater;
import com.fancypants.usage.UsageScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataScanMe.class,
		DeviceScanMe.class, UsageScanMe.class, MessageScanMe.class,
		StormScanMe.class, StormProcessingScanMe.class })
public class RecordsConfig {
	private final static Logger LOG = LoggerFactory
			.getLogger(RecordsConfig.class);
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

	@SuppressWarnings("rawtypes")
	@Autowired
	private ITridentSpout spout;

	@PostConstruct
	public void init() throws Exception {
		LOG.trace("RecordsConfig.init enter");

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
				usageStateUpdater);
		node4.partitionPersist(topicNotifierStateFactory, new Fields("result"),
				topicNotifierUpdater);

		// create the config
		Config conf = new Config();
		// for now create a local cluster
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY, conf, topology.build());

		LOG.trace("RecordsConfig.init exit");
	}
}
