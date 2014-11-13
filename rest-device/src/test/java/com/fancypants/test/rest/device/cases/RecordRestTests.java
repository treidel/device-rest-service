package com.fancypants.test.rest.device.cases;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.data.device.repository.RawRecordRepository;
import com.fancypants.rest.device.Application;
import com.fancypants.rest.device.resource.RawRecordResource;
import com.fancypants.rest.domain.RawRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		TestConfiguration.class })
@WebAppConfiguration
@IntegrationTest
public class RecordRestTests {

	private static final URI BASE_URL = URI
			.create("https://localhost:8443/device/records");

	@Autowired
	@Qualifier("deviceRestTemplate")
	private RestTemplate deviceRestTemplate;

	@Autowired
	private RawRecordRepository recordRespitory;

	@Autowired
	private DeviceRepository deviceRepository;

	@PostConstruct
	public void setup() {
		// cleanup databases
		deviceRepository.delete(DeviceEntity.TEST.DEVICEENTITY.getDevice());
		recordRespitory.deleteAllForDevice(DeviceEntity.TEST.DEVICEENTITY
				.getDevice());
		// inject
		deviceRepository.save(DeviceEntity.TEST.DEVICEENTITY);
	}

	@Test
	public void createRecordTest() {
		HttpEntity<Collection<RawRecord>> entity = new HttpEntity<Collection<RawRecord>>(
				Arrays.asList(RawRecord.TEST.RECORDS));
		ResponseEntity<Collection<RawRecordResource>> response = deviceRestTemplate
				.exchange(
						BASE_URL,
						HttpMethod.POST,
						entity,
						new ParameterizedTypeReference<Collection<RawRecordResource>>() {
						});
		Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
	}

	@Test
	public void duplicateRecordTest() {
		HttpEntity<Collection<RawRecord>> entity = new HttpEntity<Collection<RawRecord>>(
				Arrays.asList(RawRecord.TEST.RECORDS));
		// create once
		ResponseEntity<Collection<RawRecordResource>> response = deviceRestTemplate
				.exchange(
						BASE_URL,
						HttpMethod.POST,
						entity,
						new ParameterizedTypeReference<Collection<RawRecordResource>>() {
						});
		Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
		// create again
		response = deviceRestTemplate
				.exchange(
						BASE_URL,
						HttpMethod.POST,
						entity,
						new ParameterizedTypeReference<Collection<RawRecordResource>>() {
						});
	}
}
