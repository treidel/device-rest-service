package com.fancypants.rest.app.test.cases;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.rest.app.application.Application;
import com.fancypants.rest.app.resource.PowerConsumptionRecordResource;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class})
@WebAppConfiguration
@IntegrationTest
public class DeviceTest {
	private static URI BASE_URL = URI.create("http://localhost:8080/app/usage");
	private static final DeviceEntity DEVICEENTITY;
	private static final Credentials CREDENTIALS;

	static {
		// setup the circuits + test measurements
		Set<CircuitEntity> circuits = new HashSet<CircuitEntity>();
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			CircuitEntity circuit = new CircuitEntity(i, "1-" + i, 120.0f,
					10.0f);
			circuits.add(circuit);
		}
		// setup the device
		DEVICEENTITY = new DeviceEntity("ABCD1234", "000000001", circuits,
				new Date());
		CREDENTIALS = new UsernamePasswordCredentials(DEVICEENTITY.getDevice(),
				DEVICEENTITY.getSerialNumber());
	}

	private final RestTemplate restTemplate;
	
	@Autowired
	private DeviceRepository deviceRepository;

	public DeviceTest() {
		// create an HttpClient
		HttpClientBuilder builder = HttpClientBuilder.create();
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, CREDENTIALS);
		HttpClient client = builder.build();
		// create the HttpClient factory
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		// create the object mapper
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setDateFormat(new ISO8601DateFormat());
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.registerModule(new Jackson2HalModule());
		// create the message converter
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
		jsonMessageConverter.setObjectMapper(objectMapper);
		jsonMessageConverter.setSupportedMediaTypes(MediaType
				.parseMediaTypes("application/hal+json,application/json"));
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(
				1);
		messageConverters.add(jsonMessageConverter);
		// create the REST template
		this.restTemplate = new RestTemplate(factory);
		restTemplate.setMessageConverters(messageConverters);
	}

	@PostConstruct
	public void init() {
		deviceRepository.deleteAll();
		deviceRepository.save(DEVICEENTITY);
	}
	
	@Test
	public void queryHourlyTest() {
		ResponseEntity<Collection<PowerConsumptionRecordResource>> response = restTemplate
				.exchange(
						BASE_URL,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<Collection<PowerConsumptionRecordResource>>() {
						});
		Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
	}
}
