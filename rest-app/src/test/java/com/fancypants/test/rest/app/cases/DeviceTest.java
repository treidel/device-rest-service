package com.fancypants.test.rest.app.cases;

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

import com.fancypants.data.repository.DeviceRepository;
import com.fancypants.rest.app.config.RESTAppWebConfig;
import com.fancypants.rest.app.resource.DeviceResource;
import com.fancypants.test.data.values.DeviceValues;
import com.fancypants.test.rest.app.config.TestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { RESTAppWebConfig.class, TestConfiguration.class })
@WebAppConfiguration
@IntegrationTest
public class DeviceTest {
	private static final URI BASE_URL = URI.create("http://localhost:8002/app/device");
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	public void init() {
		deviceRepository.delete(DeviceValues.DEVICEENTITY.getDevice());
		deviceRepository.save(DeviceValues.DEVICEENTITY);
	}

	@Test
	public void queryDeviceTest() {
		// query the device
		ResponseEntity<DeviceResource> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null,
				new ParameterizedTypeReference<DeviceResource>() {
				});
		Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
	}

	@Test
	public void updateDeviceTest() {
		// query the device
		ResponseEntity<DeviceResource> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null,
				new ParameterizedTypeReference<DeviceResource>() {
				});
		Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
		// update the device
		restTemplate.put(BASE_URL, response.getBody().device);
	}
}
