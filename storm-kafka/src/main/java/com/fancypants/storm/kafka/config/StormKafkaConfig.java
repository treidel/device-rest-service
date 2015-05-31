package com.fancypants.storm.kafka.config;

import java.util.Properties;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import storm.kafka.bolt.KafkaBolt;
import storm.kafka.bolt.selector.DefaultTopicSelector;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.kafka.trident.TridentKafkaState;
import storm.trident.spout.IOpaquePartitionedTridentSpout;
import backtype.storm.Config;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;

import com.fancypants.common.CommonScanMe;
import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.storm.StormScanMe;
import com.fancypants.storm.config.AbstractTopologyConfig;
import com.fancypants.storm.kafka.StormKafkaScanMe;
import com.fancypants.storm.kafka.mapper.RawRecordToKafkaMapper;
import com.fancypants.storm.kafka.scheme.RawRecordScheme;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, StormScanMe.class,
		StormKafkaScanMe.class })
public class StormKafkaConfig {
	private final static Logger LOG = LoggerFactory
			.getLogger(StormKafkaConfig.class);

	private static final String KAFKA_TOPIC_ENVVAR = "KAFKA_TOPIC";
	private static final String KAFKA_BROKERS_ENVVAR = "KAFKA_BROKERS";
	private static final String ZOOKEEPER_ENVVAR = "ZOOKEEPER";

	@Autowired
	private RawRecordScheme rawRecordScheme;

	@Autowired
	private RawRecordToKafkaMapper kafkaMapper;

	@SuppressWarnings("rawtypes")
	@Bean(name = AbstractTopologyConfig.TRIDENT_SPOUT_NAME)
	@Lazy
	public Pair<Config, IOpaquePartitionedTridentSpout> tridentSpout() {
		LOG.trace("tridentSpout enter");
		// get the zookeeper host
		String zookeeperEndpoint = ConfigUtils
				.retrieveEnvVarOrFail(ZOOKEEPER_ENVVAR);
		// create the kafka spout
		BrokerHosts zk = new ZkHosts(zookeeperEndpoint);
		TridentKafkaConfig kafkaSpoutConf = new TridentKafkaConfig(zk,
				ConfigUtils.retrieveEnvVarOrFail(KAFKA_TOPIC_ENVVAR));
		kafkaSpoutConf.scheme = rawRecordScheme;
		OpaqueTridentKafkaSpout kafkaSpout = new OpaqueTridentKafkaSpout(
				kafkaSpoutConf);
		// create the pair
		Pair<Config, IOpaquePartitionedTridentSpout> pair = new ImmutablePair<Config, IOpaquePartitionedTridentSpout>(
				new Config(), kafkaSpout);
		LOG.trace("tridentSpout exit {}", pair);
		return pair;
	}

	@Bean(name = AbstractTopologyConfig.STORM_SPOUT_NAME)
	@Lazy
	public Pair<Config, IRichSpout> stormSpout(@Value("${" + KAFKA_TOPIC_ENVVAR
			+ "}") String topic,
			@Value("${" + ZOOKEEPER_ENVVAR + "}") String zookeeper) {
		LOG.trace("stormSpout enter");
		// create the kafka spout
		BrokerHosts zk = new ZkHosts(zookeeper);
		SpoutConfig spoutConfig = new SpoutConfig(zk, topic, "/" + topic,
				AbstractTopologyConfig.STORM_SPOUT_NAME);
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		IRichSpout kafkaSpout = new KafkaSpout(spoutConfig);
		// create the pair
		Pair<Config, IRichSpout> pair = new ImmutablePair<Config, IRichSpout>(
				new Config(), kafkaSpout);
		LOG.trace("stormSpout exit {}", pair);
		return pair;
	}

	@Bean(name = AbstractTopologyConfig.OUTPUT_BOLT_NAME)
	@Lazy
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Pair<Config, IRichBolt> outputBolt(@Value("${" + KAFKA_TOPIC_ENVVAR
			+ "}") String topic,
			@Value("${" + KAFKA_BROKERS_ENVVAR + "}") String brokers) {
		LOG.trace("filteredBolt enter");
		KafkaBolt kafkaBolt = new KafkaBolt().withTopicSelector(
				new DefaultTopicSelector(topic)).withTupleToKafkaMapper(
				kafkaMapper);
		Properties props = new Properties();
		props.put("metadata.broker.list", brokers);
		props.put("request.required.acks", "1");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		Config config = new Config();
		config.put(TridentKafkaState.KAFKA_BROKER_PROPERTIES, props);
		Pair<Config, IRichBolt> pair = new ImmutablePair<Config, IRichBolt>(
				config, kafkaBolt);
		LOG.trace("kafkaBolt exit {}", pair);
		return pair;
	}
}
