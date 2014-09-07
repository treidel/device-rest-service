package com.fancypants.processing.storm.device.record;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.stormspout.KinesisSpout;
import com.amazonaws.services.kinesis.stormspout.KinesisSpoutConfig;

import storm.trident.TridentTopology;
import backtype.storm.Config;
import backtype.storm.LocalCluster;

public class Processor {

	private final static String TOPOLOGY = "raw_records";
	private final static String TXID = "records";
	private final static String STREAM = "raw";
	private final static String ZOOKEEPER_ENDPOINT = "localhost:2181";
	private final static String ZOOKEEPER_PREFIX = "kinesis_spout";

	public static void main(String[] args) throws Exception {
		// create the topology
		TridentTopology topology = new TridentTopology();
		// setup the spout config
		final KinesisSpoutConfig config = new KinesisSpoutConfig(STREAM,
				ZOOKEEPER_ENDPOINT).withZookeeperPrefix(ZOOKEEPER_PREFIX);
		// create the spout
		final KinesisSpout spout = new KinesisSpout(config,
				new DefaultAWSCredentialsProviderChain(), new ClientConfiguration());
		// setup the stream
		topology.newStream(TXID, spout);
		// create the config
		Config conf = new Config();
		// for now create a local cluster
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY, conf, topology.build());
	}
}
