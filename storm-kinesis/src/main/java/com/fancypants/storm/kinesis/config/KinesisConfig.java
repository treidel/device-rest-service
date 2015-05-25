package com.fancypants.storm.kinesis.config;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import backtype.storm.Config;
import backtype.storm.topology.IRichSpout;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.stormspout.InitialPositionInStream;
import com.amazonaws.services.kinesis.stormspout.KinesisSpout;
import com.amazonaws.services.kinesis.stormspout.KinesisSpoutConfig;
import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.storm.config.AbstractTopologyConfig;
import com.fancypants.storm.kinesis.scheme.RawRecordScheme;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KinesisConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(KinesisConfig.class);

	private static final String ZOOKEEPER_ENDPOINT_ENVVAR = "ZOOKEEPER_ENDPOINT";
	private static final String AMAZON_KINESIS_STREAM_ENVVAR = "AWS_KINESIS_STREAM";
	private static final String ZOOKEEPER_PREFIX = "kinesis_spout";

	@Autowired
	private ObjectMapper objectMapper;

	@Bean(name = AbstractTopologyConfig.STORM_SPOUT_NAME)
	public Pair<Config, IRichSpout> spout() {
		LOG.trace("spout enter");
		// fetch the stream name
		String streamName = ConfigUtils
				.retrieveEnvVarOrFail(AMAZON_KINESIS_STREAM_ENVVAR);
		// fetch the zookeeper endpoint
		String zookeeperEndpoint = ConfigUtils
				.retrieveEnvVarOrFail(ZOOKEEPER_ENDPOINT_ENVVAR);
		// setup the spout config
		KinesisSpoutConfig config = new KinesisSpoutConfig(streamName,
				zookeeperEndpoint).withZookeeperPrefix(ZOOKEEPER_PREFIX)
				.withKinesisRecordScheme(new RawRecordScheme(objectMapper))
				.withInitialPositionInStream(InitialPositionInStream.LATEST);
		// create the spout
		KinesisSpout spout = new KinesisSpout(config,
				new DefaultAWSCredentialsProviderChain(),
				new ClientConfiguration());
		// setup the pair
		Pair<Config, IRichSpout> value = new ImmutablePair<Config, IRichSpout>(
				new Config(), spout);
		// done
		LOG.trace("spout exit {}", value);
		return value;
	}
}
