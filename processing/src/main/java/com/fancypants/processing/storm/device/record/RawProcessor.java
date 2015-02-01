package com.fancypants.processing.storm.device.record;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.fluent.GroupedStream;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.kinesis.stormspout.InitialPositionInStream;
import com.amazonaws.services.kinesis.stormspout.KinesisSpout;
import com.amazonaws.services.kinesis.stormspout.KinesisSpoutConfig;
import com.fancypants.common.application.Application;
import com.fancypants.data.device.DataDeviceScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.processing.storm.device.record.aggregate.HourlyEnergyCalculationAggregator;
import com.fancypants.processing.storm.device.record.aggregate.UsageAggregator;
import com.fancypants.processing.storm.device.record.auth.CustomAWSCredentialsProvider;
import com.fancypants.processing.storm.device.record.filter.PrintFilter;
import com.fancypants.processing.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.processing.storm.device.record.mapping.EnergyConsumptionTupleMapper;
import com.fancypants.processing.storm.device.record.mapping.RawRecordTupleMapper;
import com.fancypants.processing.storm.device.record.scheme.RawRecordScheme;
import com.fancypants.processing.storm.device.record.state.TopicNotifierStateFactory;
import com.fancypants.processing.storm.device.record.state.TopicNotifierStateUpdater;
import com.fancypants.processing.storm.device.record.state.UsageStateFactory;
import com.fancypants.processing.storm.device.record.state.UsageStateUpdater;
import com.fancypants.usage.UsageScanMe;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { DataDeviceScanMe.class,
		DataDeviceDynamoDBScanMe.class, DeviceScanMe.class, UsageScanMe.class,
		RawProcessor.class })
public class RawProcessor extends Application {

	private final static String TOPOLOGY = "raw_records";
	private final static String TXID = "records";
	private final static String ZOOKEEPER_ENDPOINT = "localhost:2000";
	private final static String ZOOKEEPER_PREFIX = "kinesis_spout";

	private final static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private HourlyRecordRepository hourlyRepository;

	@Autowired
	private CustomAWSCredentialsProvider credentialsProvider;

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

	@Autowired(required = false)
	@Qualifier("streamInitialization")
	private Void streamInitialization;

	@PostConstruct
	public void init() throws Exception {
		// fetch the stream name
		String streamName = System
				.getProperty("amazon.kinesis.stream.rawrecord");
		// create the topology
		TridentTopology topology = new TridentTopology();
		// setup the spout config
		final KinesisSpoutConfig config = new KinesisSpoutConfig(streamName,
				ZOOKEEPER_ENDPOINT)
				.withZookeeperPrefix(ZOOKEEPER_PREFIX)
				.withKinesisRecordScheme(new RawRecordScheme(objectMapper))
				.withInitialPositionInStream(
						InitialPositionInStream.TRIM_HORIZON);
		// create the spout
		final KinesisSpout spout = new KinesisSpout(config,
				credentialsProvider, new ClientConfiguration());
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

	public static void main(String[] args) {
		SpringApplication.run(RawProcessor.class, args);
	}
}
