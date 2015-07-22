package com.fancypants.test.message.rabbitmq.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.Assert;

import com.fancypants.common.CommonScanMe;
import com.fancypants.message.rabbitmq.MessageRabbitMQScanMe;
import com.fancypants.message.rabbitmq.config.RabbitMQConfig;
import com.fancypants.test.message.TestMessageScanMe;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class,
		TestMessageScanMe.class, MessageRabbitMQScanMe.class })
@PropertySource("classpath:/test.properties")
public class RabbitMQMessageTestConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitMQMessageTestConfig.class);

	private static final String DOCKER_IMAGE = "rabbitmq";

	private @Autowired
	@Value("${" + RabbitMQConfig.RABBITMQ_EXCHANGE_ENVVAR + "}")
	String exchange;

	private @Autowired
	@Value("${" + RabbitMQConfig.RABBITMQ_PASSWORD_ENVVAR + "}")
	String password;

	private @Autowired
	@Value("${" + RabbitMQConfig.RABBITMQ_URI_ENVVAR + "}")
	String uri;

	private String containerId = null;

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
		List<Image> images = docker.listImages();
		for (Image image : images) {
			if (image.repoTags().contains(DOCKER_IMAGE + ":latest")) {
				found = true;
			}
		}

		if (false == found) {
			LOG.info("pulling image {}", DOCKER_IMAGE);
			docker.pull(DOCKER_IMAGE);
		}
		// setup the container
		final String[] ports = { "5672" };
		final ContainerConfig config = ContainerConfig.builder()
				.image(DOCKER_IMAGE).exposedPorts(ports).build();
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

		// wait until we are able to connect to proceed
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri(uri);

		// ensure we can connect before proceeding
		Connection connection = null;
		for (int count = 0; count < 10; count++) {
			try {
				connection = factory.newConnection();
			} catch (IOException e) {
				LOG.warn("waiting to connect");
				Thread.sleep(1000);
			}
		}
		// make sure we were able to connect
		Assert.notNull(connection);
		// disconnect
		connection.close();
	}

	@PreDestroy
	private void cleanup() throws Exception {
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
