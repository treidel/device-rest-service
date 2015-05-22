package com.fancypants.storm.duplicate.config;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.DataScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.message.MessageScanMe;
import com.fancypants.storm.StormScanMe;
import com.fancypants.storm.config.TopologyUploader;
import com.fancypants.storm.duplicate.StormDuplicateScanMe;
import com.fancypants.storm.duplicate.bolt.DuplicateDetectionBolt;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataScanMe.class,
		DeviceScanMe.class, MessageScanMe.class, StormScanMe.class,
		StormDuplicateScanMe.class })
public class DuplicateConfig extends TopologyUploader {

	private final static Logger LOG = LoggerFactory
			.getLogger(DuplicateConfig.class);

	private final static String STORM_TOPOLOGY = "raw_records_topology";
	private final static String RAW_RECORDS_SPOUT = "raw_record_spout";
	private final static String DUPLICATE_DETECTION_BOLT = "duplicate_detection_bolt";
	private final static String PERSIST_BOLT = "persist_bolt";

	@Autowired
	private DuplicateDetectionBolt duplicateDetectionBolt;

	@Autowired
	private IRichSpout kinesisSpout;

	@Autowired
	private Pair<Config, IRichBolt> filteredBolt;

	@Bean
	public Pair<Config, StormTopology> duplicateTopology() {
		LOG.trace("duplicateTopology enter");

		// config object for all topologies
		Config config = new Config();

		// populate the spout and bolt config
		config.putAll(filteredBolt.getKey());

		// create the regular storm topology
		TopologyBuilder stormTopology = new TopologyBuilder();

		// setup the raw topology
		stormTopology.setSpout(RAW_RECORDS_SPOUT, kinesisSpout);
		// duplicate detection takes records from the spout
		stormTopology.setBolt(DUPLICATE_DETECTION_BOLT, duplicateDetectionBolt)
				.shuffleGrouping(RAW_RECORDS_SPOUT);
		// the kafka bolt takes records from the duplicate detection
		stormTopology.setBolt(PERSIST_BOLT, filteredBolt.getValue())
				.shuffleGrouping(DUPLICATE_DETECTION_BOLT);
		Pair<Config, StormTopology> pair = new ImmutablePair<Config, StormTopology>(
				config, stormTopology.createTopology());
		LOG.trace("duplicateTopology exit {}", pair);
		return pair;
	}

	@PostConstruct
	@ConditionalOnMissingBean(LocalCluster.class)
	public void init() throws Exception {
		LOG.trace("init enter");

		// get the topoplogy
		Pair<Config, StormTopology> duplicateTopology = duplicateTopology();

		// send the topology to the cluster
		uploadAndReplaceTopology(STORM_TOPOLOGY, duplicateTopology.getKey(),
				duplicateTopology.getValue());

		LOG.trace("init exit");
	}
}
