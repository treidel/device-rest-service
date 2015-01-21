package com.fancypants.test.rest.device.cases;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fancypants.common.application.Application;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.rest.device.resource.DeviceResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		TestConfiguration.class })
@WebAppConfiguration
@IntegrationTest
public class DeviceRestTests {

	private static final URI BASE_URL = URI
			.create("https://localhost:8443/device");

	@Autowired
	@Qualifier("deviceRestTemplate")
	private RestTemplate deviceRestTemplate;

	@Autowired
	private DeviceRepository deviceRepository;

	@PostConstruct
	public void init() {
		// start clean
		deviceRepository.delete(DeviceEntity.TEST.DEVICEENTITY.getDevice());
		// pre-create test device
		deviceRepository.save(DeviceEntity.TEST.DEVICEENTITY);
	}

	@Test
	public void queryDevice() {
		// now query for the device
		ResponseEntity<DeviceResource> response = deviceRestTemplate.exchange(
				BASE_URL, HttpMethod.GET, null,
				new ParameterizedTypeReference<DeviceResource>() {
				});
		Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
	}

	@Test
	public void updateDevice() {
		// now query for the device
		ResponseEntity<DeviceResource> response = deviceRestTemplate.exchange(
				BASE_URL, HttpMethod.GET, null,
				new ParameterizedTypeReference<DeviceResource>() {
				});
		// now update using the same device
		deviceRestTemplate.put(BASE_URL, response.getBody().device);
	}
}
