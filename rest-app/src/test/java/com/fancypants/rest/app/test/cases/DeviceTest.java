package com.fancypants.rest.app.test.cases;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.rest.app.application.Application;
import com.fancypants.rest.app.resource.DeviceResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		TestConfiguration.class })
@WebAppConfiguration
@IntegrationTest
public class DeviceTest {
	private static final URI BASE_URL = URI
			.create("http://localhost:8080/app/device");
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	public void init() {
		deviceRepository.delete(DeviceEntity.TEST.DEVICEENTITY.getDevice());
		deviceRepository.save(DeviceEntity.TEST.DEVICEENTITY);
	}

	@Test
	public void queryDeviceTest() {
		// query the device
		ResponseEntity<DeviceResource> response = restTemplate
				.exchange(
						BASE_URL,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<DeviceResource>() {
						});
		Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
	}
	
	@Test
	public void updateDeviceTest() {
		// query the device
		ResponseEntity<DeviceResource> response = restTemplate
				.exchange(
						BASE_URL,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<DeviceResource>() {
						});
		Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
		// update the device
		restTemplate.put(BASE_URL, response.getBody().device);
	}
}
