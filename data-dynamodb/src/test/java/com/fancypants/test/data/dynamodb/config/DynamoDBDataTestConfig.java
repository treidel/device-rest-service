package com.fancypants.test.data.dynamodb.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fancypants.common.CommonScanMe;
import com.fancypants.test.data.dynamodb.TestDynamoDBDataScanMe;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, TestDynamoDBDataScanMe.class })
@PropertySource("classpath:/test.properties")
public class DynamoDBDataTestConfig {

	private static final Logger LOG = LoggerFactory.getLogger(DynamoDBDataTestConfig.class);

	private static final String DOCKER_IMAGE = "reideltj/dynamodb";

	@Autowired
	private DynamoDB dynamoDB;

	private DockerClient docker = null;
	private String containerId = null;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@PostConstruct
	public void init() throws Exception {

		LOG.trace("init enter");

		// create the docker client
		docker = new DefaultDockerClient("unix:///var/run/docker.sock");

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

		// go through existing containers and kill any stale ones
		List<Container> containers = docker.listContainers();
		for (Container container : containers) {
			if (true == container.image().equals(DOCKER_IMAGE)) {
				LOG.info("killing stale container", container.id());
				docker.stopContainer(container.id(), 30);
				docker.removeContainer(container.id());
			}
		}

		// setup the container
		final String[] ports = { "8000" };
		final ContainerConfig config = ContainerConfig.builder().image(DOCKER_IMAGE).exposedPorts(ports).build();
		// bind container ports to host ports
		final Map<String, List<PortBinding>> portBindings = new HashMap<>();
		for (String port : ports) {
			List<PortBinding> hostPorts = new ArrayList<>();
			hostPorts.add(PortBinding.of("0.0.0.0", port));
			portBindings.put(port, hostPorts);
		}
		final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

		LOG.info("creating container");
		final ContainerCreation creation = docker.createContainer(config);
		containerId = creation.id();
		LOG.info("container {} created", containerId);

		// Start container
		LOG.info("starting container {}", containerId);
		docker.startContainer(containerId, hostConfig);

		// wait until we can query the list of tables
		dynamoDB.listTables();
	}

	@PreDestroy
	private void cleanup() throws Exception {
		LOG.trace("fini enter");
		if (null != docker) {
			if (null != containerId) {
				// stop the container
				LOG.info("stopping container {}", containerId);
				docker.stopContainer(containerId, 30);
				LOG.info("removing container {}", containerId);
				// remove the container
				docker.removeContainer(containerId);
			}
		}
		LOG.trace("fini exit");
	}
}
