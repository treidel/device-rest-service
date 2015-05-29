package com.fancypants.test.stream.kafka.suite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.admin.TopicCommand;
import kafka.admin.TopicCommand.TopicCommandOptions;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.test.stream.kafka.config.KafkaStreamTestConfig;
import com.fancypants.test.stream.suite.StreamTests;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, loader = AnnotationConfigContextLoader.class, classes = KafkaStreamTestConfig.class, initializers = ConfigFileApplicationContextInitializer.class)
public class KafkaStreamTests extends StreamTests {

	private static final Logger LOG = LoggerFactory
			.getLogger(KafkaStreamTests.class);
	private static final String DOCKER_IMAGE = "spotify/kafka";
	private static final String ZOOKEEPER = "localhost:2181";
	private static final String TOPIC = "test";

	private static String containerId;

	@BeforeClass
	public static void init() throws DockerCertificateException,
			DockerException, InterruptedException {
		LOG.trace("init enter");

		// create the docker client
		final DockerClient docker = DefaultDockerClient.fromEnv().build();

		// boolean to indicate if the images already exists
		boolean found = false;
		for (Image image : docker.listImages()) {
			if (image.repoTags().contains("spotify/kafka:latest")) {
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
				.image("spotify/kafka").exposedPorts(ports)
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
		zkClient.setZkSerializer(new CustomSerializer());

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

	@AfterClass
	public static void fini() throws DockerCertificateException,
			DockerException, InterruptedException {
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

	private static final class CustomSerializer implements ZkSerializer {
		@Override
		public byte[] serialize(Object o) throws ZkMarshallingError {
			return ZKStringSerializer.serialize(o);
		}

		@Override
		public Object deserialize(byte[] bytes) throws ZkMarshallingError {
			return ZKStringSerializer.deserialize(bytes);
		}
	}
}
