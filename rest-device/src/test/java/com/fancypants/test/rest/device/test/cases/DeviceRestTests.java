package com.fancypants.test.rest.device.test.cases;

import java.net.URI;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.rest.device.Application;
import com.fancypants.rest.device.resource.DeviceResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, TestConfiguration.class})
@WebAppConfiguration
@IntegrationTest
public class DeviceRestTests {

	private static final URI BASE_URL = URI
			.create("https://localhost:8443/device");

	private static final DeviceEntity DEVICE1;

	static {
		// setup the test records
		Set<CircuitEntity> circuits = new TreeSet<CircuitEntity>();
		for (int i = 1; i <= 16; i++) {
			CircuitEntity circuit = new CircuitEntity(i, String.valueOf(i),
					120.0f, 30.0f);
			circuits.add(circuit);
		}
		DEVICE1 = new DeviceEntity("ABCD1234", "00000001", circuits, new Date());
	}

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void createDeviceTest() {
		ResponseEntity<DeviceResource> resource = restTemplate.postForEntity(BASE_URL, DEVICE1, DeviceResource.class);
		Assert.assertTrue(HttpStatus.ACCEPTED.equals(resource.getStatusCode()));
	}
}
