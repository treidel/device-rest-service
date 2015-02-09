package com.fancypants.test.rest.app.cases;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

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
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.rest.app.config.WebConfig;
import com.fancypants.rest.app.resource.PowerConsumptionRecordResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { WebConfig.class,
		TestConfiguration.class })
@WebAppConfiguration
@IntegrationTest
public class UsageTest {
	private static final URI BASE_URL = URI
			.create("http://localhost:8080/app/usage");
	private static final URI HOURLY_URL = URI.create(BASE_URL + "/hourly");

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private HourlyRecordRepository usageRepository;

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	public void init() {
		deviceRepository.delete(DeviceEntity.TEST.DEVICEENTITY.getDevice());
		usageRepository.deleteAllForDevice(DeviceEntity.TEST.DEVICEENTITY
				.getDevice());
		deviceRepository.save(DeviceEntity.TEST.DEVICEENTITY);
		usageRepository.save(Arrays
				.asList(EnergyConsumptionRecordEntity.TEST.RECORDS));
	}

	@Test
	public void queryHourlyTest() {
		// query the hourly data
		ResponseEntity<Collection<PowerConsumptionRecordResource>> recordsResponse = restTemplate
				.exchange(
						HOURLY_URL,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<Collection<PowerConsumptionRecordResource>>() {
						});
		Assert.assertTrue(HttpStatus.OK.equals(recordsResponse.getStatusCode()));
		Assert.assertTrue(recordsResponse.getBody().size() == EnergyConsumptionRecordEntity.TEST.RECORDS.length);
		// read each individual record too
		for (PowerConsumptionRecordResource resource : recordsResponse
				.getBody()) {
			ResponseEntity<PowerConsumptionRecordResource> recordResponse = restTemplate
					.exchange(
							URI.create(resource.getLink("self").getHref()),
							HttpMethod.GET,
							null,
							new ParameterizedTypeReference<PowerConsumptionRecordResource>() {
							});
			Assert.assertTrue(HttpStatus.OK.equals(recordResponse
					.getStatusCode()));
		}
	}
}
