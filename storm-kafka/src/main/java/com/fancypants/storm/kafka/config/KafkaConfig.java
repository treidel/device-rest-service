package com.fancypants.storm.kafka.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import storm.kafka.BrokerHosts;
import storm.kafka.ZkHosts;
import storm.kafka.bolt.KafkaBolt;
import storm.kafka.bolt.selector.DefaultTopicSelector;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.kafka.trident.TridentKafkaState;
import storm.trident.spout.IOpaquePartitionedTridentSpout;
import backtype.storm.Config;
import backtype.storm.topology.IRichBolt;

import com.fancypants.common.CommonScanMe;
import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.storm.StormScanMe;
import com.fancypants.storm.kafka.StormKafkaScanMe;
import com.fancypants.storm.kafka.mapper.RawRecordToKafkaMapper;
import com.fancypants.storm.kafka.scheme.RawRecordScheme;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, StormScanMe.class,
		StormKafkaScanMe.class })
public class KafkaConfig {
	private final static Logger LOG = LoggerFactory
			.getLogger(KafkaConfig.class);

	private static final String KAFKA_TOPIC_ENVVAR = "KAFKA_TOPIC";
	private static final String KAFKA_BROKERS_ENVVAR = "KAFKA_BROKERS";
	private static final String ZOOKEEPER_ENDPOINT_ENVVAR = "ZOOKEEPER_ENDPOINT";

	@Autowired
	private RawRecordScheme rawRecordScheme;

	@Autowired
	private RawRecordToKafkaMapper kafkaMapper;

	@SuppressWarnings("rawtypes")
	@Bean
	public IOpaquePartitionedTridentSpout filteredSpout() {
		LOG.trace("filteredSpout enter");
		// get the zookeeper host
		String zookeeperEndpoint = ConfigUtils
				.retrieveEnvVarOrFail(ZOOKEEPER_ENDPOINT_ENVVAR);
		// create the kafka spout
		BrokerHosts zk = new ZkHosts(zookeeperEndpoint);
		TridentKafkaConfig kafkaSpoutConf = new TridentKafkaConfig(zk,
				ConfigUtils.retrieveEnvVarOrFail(KAFKA_TOPIC_ENVVAR));
		kafkaSpoutConf.scheme = rawRecordScheme;
		OpaqueTridentKafkaSpout kafkaSpout = new OpaqueTridentKafkaSpout(
				kafkaSpoutConf);

		LOG.trace("filteredSpout exit", kafkaSpout);
		return kafkaSpout;
	}

	@Bean
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IRichBolt filteredBolt() {
		LOG.trace("filteredBolt enter");
		KafkaBolt kafkaBolt = new KafkaBolt().withTopicSelector(
				new DefaultTopicSelector(ConfigUtils
						.retrieveEnvVarOrFail(KafkaConfig.KAFKA_TOPIC_ENVVAR)))
				.withTupleToKafkaMapper(kafkaMapper);
		LOG.trace("kafkaBolt exit", kafkaBolt);
		return kafkaBolt;
	}

	@Bean
	public Config filteredConfig() {
		LOG.trace("filteredConfig enter");
		Properties props = new Properties();
		props.put("metadata.broker.list", ConfigUtils
				.retrieveEnvVarOrFail(KafkaConfig.KAFKA_BROKERS_ENVVAR));
		props.put("request.required.acks", "1");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		Config config = new Config();
		config.put(TridentKafkaState.KAFKA_BROKER_PROPERTIES, props);
		LOG.trace("filteredConfig exit {}", config);
		return config;
	}
}
