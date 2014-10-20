package com.fancypants.test.rest.device.test.cases;

import java.net.URI;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.rest.device.Application;
import com.fancypants.rest.device.resource.DeviceResource;
import com.fancypants.rest.domain.Circuit;
import com.fancypants.rest.domain.Device;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		TestConfiguration.class })
@WebAppConfiguration
@IntegrationTest
public class AdminRestTests {

	private static final URI BASE_URL = URI
			.create("https://localhost:8443/admin");
	private static final URI DEVICE_URL = URI.create(BASE_URL.toString()
			+ "/devices");

	private static final Device DEVICE1;

	static {
		// setup the test records
		SortedSet<Circuit> circuits = new TreeSet<Circuit>();
		for (int i = 1; i <= 16; i++) {
			Circuit circuit = new Circuit(String.valueOf(i) + "-1", 120.0f,
					30.0f);
			circuits.add(circuit);
		}
		DEVICE1 = new Device("ABCD1234", "00000001", circuits);
	}

	@Autowired
	@Qualifier("adminRestTemplate")
	private RestTemplate adminRestTemplate;

	@Autowired
	private DeviceRepository deviceRepository;

	@Before
	public void cleanup() {
		deviceRepository.deleteAll();
	}

	@Test
	public void createDeviceTest() {
		// send the creation request
		ResponseEntity<Void> response = adminRestTemplate.postForEntity(
				DEVICE_URL, DEVICE1, Void.class);
		Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
	}

	@Test
	public void queryDeviceTest() {
		// first create
		ResponseEntity<Void> response = adminRestTemplate.postForEntity(
				DEVICE_URL, DEVICE1, Void.class);
		// now query for the location provided in the response
		ResponseEntity<DeviceResource> resource = adminRestTemplate
				.getForEntity(response.getHeaders().getLocation(),
						DeviceResource.class);
		Assert.assertTrue(HttpStatus.OK.equals(resource.getStatusCode()));
	}

	@Test
	public void deleteDeviceTest() {
		// first create
		ResponseEntity<Void> response = adminRestTemplate.postForEntity(
				DEVICE_URL, DEVICE1, Void.class);
		// now delete the location provided in the response
		adminRestTemplate.delete(response.getHeaders().getLocation());
	}

	@Test
	public void updateDeviceTest() {
		// first create
		ResponseEntity<Void> response = adminRestTemplate.postForEntity(
				DEVICE_URL, DEVICE1, Void.class);
		// now query for the location provided in the response
		ResponseEntity<DeviceResource> resource = adminRestTemplate
				.getForEntity(response.getHeaders().getLocation(),
						DeviceResource.class);
		// now update
		adminRestTemplate.put(response.getHeaders().getLocation(),
				resource.getBody().device);
		Assert.assertTrue(HttpStatus.OK.equals(resource.getStatusCode()));
	}
}
