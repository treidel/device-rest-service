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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fancypants.common.CommonScanMe;
import com.fancypants.data.device.dynamodb.config.DynamoDBConfig;
import com.fancypants.test.data.dynamodb.TestDynamoDBDataScanMe;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class,
		TestDynamoDBDataScanMe.class })
@PropertySource("classpath:/test.properties")
public class DynamoDBDataTestConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(DynamoDBDataTestConfig.class);

	private static final String DOCKER_IMAGE = "reideltj/dynamodb";

	private String containerId = null;

	private @Autowired
	AWSCredentials awsCredentials;

	private @Autowired
	@Qualifier(DynamoDBConfig.AMAZON_DYNAMODB_ENDPOINT_NAME)
	String endpoint;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	@Autowired
	public DynamoDB dynamoDB() {
		// create the dynamodb client
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(awsCredentials);
		amazonDynamoDB.setEndpoint(endpoint);
		// create the wrapper
		return new DynamoDB(amazonDynamoDB);
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
			if (image.repoTags().contains(DOCKER_IMAGE)) {
				found = true;
			}
		}

		if (false == found) {
			LOG.info("pulling image {}", DOCKER_IMAGE);
			docker.pull(DOCKER_IMAGE);
		}
		// setup the container
		final String[] ports = { "8000" };
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

		// wait until we can query the list of tables
		dynamoDB().listTables();
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
