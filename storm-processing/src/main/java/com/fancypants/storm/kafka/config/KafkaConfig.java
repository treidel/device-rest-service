package com.fancypants.storm.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import storm.kafka.BrokerHosts;
import storm.kafka.ZkHosts;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.spout.IOpaquePartitionedTridentSpout;

import com.fancypants.common.CommonScanMe;
import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.storm.StormScanMe;
import com.fancypants.storm.kafka.StormKafkaScanMe;
import com.fancypants.storm.kafka.scheme.RawRecordScheme;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, StormScanMe.class,
		StormKafkaScanMe.class })
public class KafkaConfig {
	private final static Logger LOG = LoggerFactory
			.getLogger(KafkaConfig.class);

	public static final String KAFKA_TOPIC_ENVVAR = "KAFKA_TOPIC";

	@Autowired
	private RawRecordScheme rawRecordScheme;

	@SuppressWarnings("rawtypes")
	@Bean
	public IOpaquePartitionedTridentSpout kafkaSpout() {
		LOG.trace("kafkaSpout enter");
		// create the kafka spout
		BrokerHosts zk = new ZkHosts("localhost");
		TridentKafkaConfig kafkaSpoutConf = new TridentKafkaConfig(zk,
				ConfigUtils.retrieveEnvVarOrFail(KAFKA_TOPIC_ENVVAR));
		kafkaSpoutConf.scheme = rawRecordScheme;
		OpaqueTridentKafkaSpout kafkaSpout = new OpaqueTridentKafkaSpout(
				kafkaSpoutConf);

		LOG.trace("kafkaSpout exit", kafkaSpout);
		return kafkaSpout;
	}
}
