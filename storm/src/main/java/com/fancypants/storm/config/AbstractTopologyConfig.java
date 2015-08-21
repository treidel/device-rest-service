package com.fancypants.storm.config;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift7.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fancypants.common.config.util.ConfigUtils;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.StormTopology;
import backtype.storm.generated.TopologySummary;
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.Utils;

@EnableAsync
public abstract class AbstractTopologyConfig {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractTopologyConfig.class);

	public static final String STORM_SPOUT_NAME = "stormSpout";
	public static final String TRIDENT_SPOUT_NAME = "tridentSpout";
	public static final String OUTPUT_BOLT_NAME = "outputBolt";

	private static final String NIMBUS_ADDRESS_ENVVAR = "NIMBUS_ADDRESS";

	@Autowired
	private ConfigurableApplicationContext appContext;

	protected abstract Pair<Config, StormTopology> getTopology();

	protected abstract String getTopologyPrefix();

	@PostConstruct
	@ConditionalOnMissingBean(LocalCluster.class)
	public void init() throws Exception {
		LOG.trace("init enter");

		// get the topology
		String topologyPrefix = getTopologyPrefix();
		Pair<Config, StormTopology> topology = getTopology();

		// submit the topology
		uploadAndReplaceTopology(topologyPrefix, topology.getLeft(), topology.getRight());

		LOG.trace("init exit");
	}

	@Bean
	public ApplicationListener<ContextRefreshedEvent> contextRefreshedListener() {
		LOG.trace("ContextRefreshedEvent enter");
		ApplicationListener<ContextRefreshedEvent> listener = new ApplicationStartedListener();
		LOG.trace("ContextRefreshedEvent exit {}", listener);
		return listener;
	}

	@Bean
	public ShutdownBean shutdownBean() {
		LOG.trace("shutdownBean enter");
		ShutdownBean bean = new ShutdownBean();
		LOG.trace("shutdownBean exit {}", bean);
		return bean;
	}

	private void uploadAndReplaceTopology(String topologyPrefix, Config topologyConfig, StormTopology topology) {
		LOG.trace("uploadAndReplaceTopology enter {}={} {}={} {}={}", "topologyPrefix", topologyPrefix,
				"topologyConfig", topologyConfig, "topology", topology);
		// compute the storm config
		Config stormConfig = computeStormConfig();
		// add it the topology's config
		stormConfig.putAll(topologyConfig);

		try {

			// get the client
			Nimbus.Iface client = NimbusClient.getConfiguredClient(stormConfig).getClient();

			// create a new topology name
			String topologyName = topologyPrefix + "-" + UUID.randomUUID().toString();

			// first query the current topologies
			ClusterSummary clusterSummary = client.getClusterInfo();

			// deploy the new topology
			StormSubmitter.submitTopology(topologyName, stormConfig, topology);

			// look at the old topologies to find old copies of this topology
			for (TopologySummary topologySummary : clusterSummary.get_topologies()) {
				// if this matches the topology prefix we remove it
				if (true == topologySummary.get_name().startsWith(topologyPrefix)) {
					LOG.info("removing topology={}", topologySummary.get_name());
					// remove
					try {
						client.killTopology(topologySummary.get_name());
					} catch (NotAliveException e) {
						LOG.warn("topology={} not active", topologySummary.get_name());
					}
				}
			}

		} catch (TException e) {
			LOG.error("thrift error", e);
		} catch (AlreadyAliveException e) {
			LOG.error("thrift error", e);
		} catch (InvalidTopologyException e) {
			LOG.error("thrift error", e);
		} catch (RuntimeException e) {
			LOG.error("runtime error", e);
			throw e;
		}

		LOG.trace("uploadAndReplaceTopology exit");
	}

	@SuppressWarnings("unchecked")
	private Config computeStormConfig() {
		LOG.trace("getStormConfig enter");
		// get the nimbus connection details
		String host = ConfigUtils.retrieveEnvVarOrFail(NIMBUS_ADDRESS_ENVVAR);
		// setup the config
		Config config = new Config();
		config.putAll(Utils.readStormConfig());
		config.put(Config.NIMBUS_HOST, host);
		config.put(Config.NIMBUS_THRIFT_PORT, 6627);
		LOG.trace("getStormConfig exit {}", config);
		return config;
	}

	public class ShutdownBean {

		@Async
		public void shutdown() {
			LOG.trace("ShutdownBean.shutdown enter");

			LOG.info("initiating shutdown");

			// close the app context
			appContext.close();
			// exit?
			System.exit(0);

			LOG.trace("ShutdownBean.shutdown exit");
		}

	}

	private class ApplicationStartedListener implements ApplicationListener<ContextRefreshedEvent> {

		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
			LOG.trace("ApplicationStartedListener.onApplicationEvent enter {}={}", "event", event);

			shutdownBean().shutdown();

			LOG.trace("ApplicationStartedListener.onApplicationEvent exit");

		}
	}
}
