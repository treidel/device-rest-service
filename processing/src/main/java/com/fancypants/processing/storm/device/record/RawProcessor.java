package com.fancypants.processing.storm.device.record;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import storm.trident.TridentTopology;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.kinesis.stormspout.KinesisSpout;
import com.amazonaws.services.kinesis.stormspout.KinesisSpoutConfig;
import com.fancypants.data.device.DataDeviceScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.processing.storm.device.record.auth.CustomAWSCredentialsProvider;
import com.fancypants.processing.storm.device.record.function.TimeGroupingFunction;
import com.fancypants.processing.storm.device.record.scheme.RawRecordScheme;
import com.fancypants.processing.storm.device.record.state.UsageStateFactory;
import com.fancypants.processing.storm.device.record.state.UsageStateUpdater;
import com.fancypants.usage.UsageScanMe;
import com.fancypants.usage.generators.HourlyDateIntervalGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { DataDeviceScanMe.class,
		DataDeviceDynamoDBScanMe.class, DeviceScanMe.class, UsageScanMe.class })
public class RawProcessor {

	private final static String TOPOLOGY = "raw_records";
	private final static String TXID = "records";
	private final static String STREAM = "raw";
	private final static String ZOOKEEPER_ENDPOINT = "localhost:2000";
	private final static String ZOOKEEPER_PREFIX = "kinesis_spout";

	private final static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private HourlyRecordRepository hourlyRepository;

	@PostConstruct
	public void init() throws Exception {
		// create the topology
		TridentTopology topology = new TridentTopology();
		// setup the spout config
		final KinesisSpoutConfig config = new KinesisSpoutConfig(STREAM,
				ZOOKEEPER_ENDPOINT).withZookeeperPrefix(ZOOKEEPER_PREFIX)
				.withKinesisRecordScheme(new RawRecordScheme(objectMapper));
		// create the spout
		final KinesisSpout spout = new KinesisSpout(config,
				new CustomAWSCredentialsProvider(), new ClientConfiguration());
		// setup the stream
		topology.newStream(TXID, spout)
				.each(new Fields("timestamp"),
						new TimeGroupingFunction(
								new HourlyDateIntervalGenerator()),
						new Fields("date"))
				.partitionPersist(new UsageStateFactory(),
						new UsageStateUpdater());
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
