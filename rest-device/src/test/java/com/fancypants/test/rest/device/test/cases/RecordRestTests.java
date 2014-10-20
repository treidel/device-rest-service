package com.fancypants.test.rest.device.test.cases;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.data.device.repository.RawRecordRepository;
import com.fancypants.rest.device.Application;
import com.fancypants.rest.device.resource.RawRecordResource;
import com.fancypants.rest.domain.RawMeasurement;
import com.fancypants.rest.domain.RawRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		TestConfiguration.class })
@WebAppConfiguration
@IntegrationTest
public class RecordRestTests {

	private static final URI BASE_URL = URI
			.create("https://localhost:8443/device/records");

	private static final DeviceEntity DEVICEENTITY;
	private static final RawRecord RECORD1;
	private static final RawRecord RECORD2;
	private static final Collection<RawRecord> RECORDS = new ArrayList<RawRecord>(
			2);

	static {
		// setup the circuits + test measurements
		Set<CircuitEntity> circuits = new HashSet<CircuitEntity>();
		Set<RawMeasurement> measurements = new HashSet<RawMeasurement>();
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			CircuitEntity circuit = new CircuitEntity(i, "1-" + i, 120.0f,
					10.0f);
			circuits.add(circuit);
			RawMeasurement measurement = new RawMeasurement(circuit.getName(),
					0.1f);
			measurements.add(measurement);
		}
		// setup the device
		DEVICEENTITY = new DeviceEntity("ABCD1234", "000000001", circuits,
				new Date());
		// setup the records
		RECORD1 = new RawRecord(UUID.randomUUID(), new Date(), measurements);
		RECORD2 = new RawRecord(UUID.randomUUID(), new Date(), measurements);
		RECORDS.add(RECORD1);
		RECORDS.add(RECORD2);
	}

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
		deviceRepository.deleteAll();
		recordRespitory.deleteAll();
		// inject
		deviceRepository.save(DEVICEENTITY);
	}

	@Test
	public void createRecordTest() {
		HttpEntity<Collection<RawRecord>> entity = new HttpEntity<Collection<RawRecord>>(
				RECORDS);
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
				RECORDS);
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
