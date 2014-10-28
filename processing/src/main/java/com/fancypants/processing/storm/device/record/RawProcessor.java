package com.fancypants.processing.storm.device.record;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import storm.trident.TridentTopology;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.kinesis.stormspout.InitialPositionInStream;
import com.amazonaws.services.kinesis.stormspout.KinesisSpout;
import com.amazonaws.services.kinesis.stormspout.KinesisSpoutConfig;
import com.fancypants.data.device.DataDeviceScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.processing.storm.device.record.aggregate.EnergyCalculationAggregator;
import com.fancypants.processing.storm.device.record.aggregate.UsageAggregator;
import com.fancypants.processing.storm.device.record.auth.CustomAWSCredentialsProvider;
import com.fancypants.processing.storm.device.record.filter.PrintFilter;
import com.fancypants.processing.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.processing.storm.device.record.mapping.EnergyConsumptionTupleMapper;
import com.fancypants.processing.storm.device.record.mapping.RawRecordTupleMapper;
import com.fancypants.processing.storm.device.record.scheme.RawRecordScheme;
import com.fancypants.processing.storm.device.record.state.UsageStateFactory;
import com.fancypants.processing.storm.device.record.state.UsageStateUpdater;
import com.fancypants.usage.UsageScanMe;
import com.fancypants.usage.generators.HourlyDateIntervalGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { DataDeviceScanMe.class,
		DataDeviceDynamoDBScanMe.class, DeviceScanMe.class, UsageScanMe.class,
		RawProcessor.class })
public class RawProcessor {

	private final static String TOPOLOGY = "raw_records";
	private final static String TXID = "records";
	private final static String ZOOKEEPER_ENDPOINT = "localhost:2000";
	private final static String ZOOKEEPER_PREFIX = "kinesis_spout";

	private final static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private HourlyRecordRepository hourlyRepository;

	@Autowired
	private CustomAWSCredentialsProvider credentialsProvider;

	@Autowired(required = false)
	@Qualifier("streamInitialization")
	private Void streamInitialization;

	@PostConstruct
	public void init() throws Exception {
		// fetch the stream name
		String streamName = System.getProperty("amazon.kinesis.stream.rawrecord");
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
		topology.newStream(TXID, spout)
				.partitionBy(new Fields(RawRecordEntity.DEVICE_ATTRIBUTE))
				.each(RawRecordTupleMapper.getOutputFields(), new PrintFilter())
				.aggregate(
						RawRecordTupleMapper.getOutputFields(),
						new EnergyCalculationAggregator(
								new HourlyDateIntervalGenerator()),
						EnergyConsumptionTupleMapper.getOutputFields())
				.each(EnergyConsumptionTupleMapper.getOutputFields(),
						new PrintFilter())
				.each(EnergyConsumptionTupleMapper.getOutputFields(),
						new PrintFilter())
				.groupBy(
						new Fields(
								EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE,
								EnergyConsumptionRecordEntity.DATE_ATTRIBUTE))
				.aggregate(
						new Fields(EnergyConsumptionEntityMapper.ATTRIBUTES),
						new UsageAggregator(), new Fields("result"))
				.each(new Fields("result"), new PrintFilter())
				.partitionPersist(new UsageStateFactory(credentialsProvider),
						new Fields("result"), new UsageStateUpdater());

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
