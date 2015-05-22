package com.fancypants.storm.local.config;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;

@Configuration
public class LocalConfig {

	private final static Logger LOG = LoggerFactory
			.getLogger(LocalConfig.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public LocalCluster localCluster() {
		// create and start local cluster
		LocalCluster cluster = new LocalCluster();
		return cluster;
	}

	@PostConstruct
	@Autowired
	private void init(LocalCluster localCluster) throws Exception {
		LOG.trace("init enter");

		// find all storm topologies
		Map<String, Pair<Config, StormTopology>> stormTopologies = applicationContext
				.getBeansOfType(Pair<Config, StormTopology>.class);
		for (StormTopology stormTopology: stormTopologies.values()) {
			localCluster.submitTopology(stormTopology.toString(), new Config(),
					stormTopology);
		}
		LOG.trace("init exit");
	}
}
