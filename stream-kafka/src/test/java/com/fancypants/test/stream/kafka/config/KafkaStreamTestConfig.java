package com.fancypants.test.stream.kafka.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import kafka.admin.AdminUtils;
import kafka.admin.TopicCommand;
import kafka.admin.TopicCommand.TopicCommandOptions;
import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.fancypants.common.CommonScanMe;
import com.fancypants.stream.kafka.StreamKafkaScanMe;
import com.fancypants.stream.kafka.config.KafkaStreamConfig;
import com.fancypants.test.stream.TestStreamScanMe;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class,
		TestStreamScanMe.class, StreamKafkaScanMe.class })
@PropertySource("classpath:/test.properties")
public class KafkaStreamTestConfig {
	private static final Logger LOG = LoggerFactory
			.getLogger(KafkaStreamTestConfig.class);

	private static final String DOCKER_IMAGE = "spotify/kafka";
	private static final String ZOOKEEPER = "localhost:2181";
	private static final String TOPIC = "test";

	private String containerId;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@PostConstruct
	public void init() throws Exception {
		LOG.trace("init enter");

		// create the docker client
		final DockerClient docker = DefaultDockerClient.fromEnv().build();

		// boolean to indicate if the images already exists
		boolean found = false;
		for (Image image : docker.listImages()) {
			if (image.repoTags().contains(DOCKER_IMAGE)) {
				found = true;
			}
		}

		if (false == found) {
			LOG.info("pulling image {}", DOCKER_IMAGE);
			docker.pull(DOCKER_IMAGE);
		}
		// setup the container
		final String[] ports = { "2181", "9092" };
		final ContainerConfig config = ContainerConfig.builder()
				.image(DOCKER_IMAGE).exposedPorts(ports)
				.env("ADVERTISED_HOST=localhost", "ADVERTISED_PORT=9092")
				.build();
		// bind container ports to host ports
		final Map<String, List<PortBinding>> portBindings = new HashMap<>();
		for (String port : ports) {
			List<PortBinding> hostPorts = new ArrayList<>();
			hostPorts.add(PortBinding.of("0.0.0.0", port));
			portBindings.put(port, hostPorts);
		}
		final HostConfig hostConfig = HostConfig.builder()
				.portBindings(portBindings).build();

		LOG.info("creating container");
		final ContainerCreation creation = docker.createContainer(config);
		containerId = creation.id();
		LOG.info("container {} created", containerId);

		// Start container
		LOG.info("starting container {}", containerId);
		docker.startContainer(containerId, hostConfig);

		// connect to zookeeper so that we can query kafka data
		ZkClient zkClient = new ZkClient(ZOOKEEPER);
		zkClient.setZkSerializer(new KafkaStreamConfig.ZkStringSerializer());

		// wait until the broker comes up
		while (0 == ZkUtils.getSortedBrokerList(zkClient).size()) {
			LOG.debug("waiting for broker to come up");
			Thread.sleep(100);
		}

		LOG.info("kafka broker up");

		// create the topic
		LOG.info("creating kafka topic={}", TOPIC);
		AdminUtils.createTopic(zkClient, TOPIC, 1, 1, new Properties());

		// query topic to be sure its ready
		String[] options = { "--topic", TOPIC };
		TopicCommand.describeTopic(zkClient, new TopicCommandOptions(options));

		// close zookeeper
		zkClient.close();

		LOG.trace("init exit");
	}

	@PreDestroy
	public void fini() throws DockerCertificateException, DockerException,
			InterruptedException {
		LOG.trace("fini enter");
		if (null != containerId) {
			// stop the container
			final DockerClient docker = DefaultDockerClient.fromEnv().build();
			LOG.info("stopping container {}", containerId);
			docker.stopContainer(containerId, 30);
			LOG.info("removing container {}", containerId);
			// remove the container
			docker.removeContainer(containerId);
		}
		LOG.trace("fini exit");
	}

}
