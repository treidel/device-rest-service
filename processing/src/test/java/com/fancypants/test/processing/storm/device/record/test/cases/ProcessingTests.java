package com.fancypants.test.processing.storm.device.record.test.cases;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.RawMeasurementEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.processing.storm.device.record.RawProcessor;
import com.fancypants.stream.writer.StreamWriter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { RawProcessor.class,
		TestConfiguration.class })
@IntegrationTest
public class ProcessingTests {

	public static final RawRecordEntity RECORD1;
	public static final RawRecordEntity RECORD2;

	private static final int SECONDS_PER_HOUR = 60 * 60;

	static {
		// setup test data
		Map<Integer, RawMeasurementEntity> circuits = new TreeMap<Integer, RawMeasurementEntity>();
		for (int i = 1; i <= 16; i++) {
			RawMeasurementEntity measurement = new RawMeasurementEntity(i,
					120.0f, 30.0f);
			circuits.put(i, measurement);
		}
		// setup the test records
		Date time2 = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time2);
		calendar.add(Calendar.HOUR, -1);
		Date time1 = calendar.getTime();
		RECORD1 = new RawRecordEntity("ABCD1234", UUID.randomUUID().toString(),
				time1, 10.0f / 60.0f, circuits);
		RECORD2 = new RawRecordEntity("ABCD1234", UUID.randomUUID().toString(),
				time2, 10.0f / 60.0f, circuits);
	}

	@Autowired
	private StreamWriter<RawRecordEntity> streamWriter;

	@Autowired
	private HourlyRecordRepository repository;

	@Autowired
	private AmazonKinesis amazonKinesis;

	@Before
	public void cleanup() {
		repository.deleteAllForDevice(DeviceEntity.TEST.DEVICEENTITY
				.getDevice());
	}

	@Test
	public void writeRecordsTest() throws InterruptedException {
		// write some test records
		streamWriter.putRecord(RECORD1.getDevice(), RECORD1);
		streamWriter.putRecord(RECORD2.getDevice(), RECORD2);
		// now we wait for the data to come through
		List<EnergyConsumptionRecordEntity> records = fetchRecordsBlocking();
		// fail if we don't have the right number records
		Assert.assertTrue(2 == records.size());
		// check that the usage is correct
		float energyInKWH = calculateEnergyInKWH(RECORD1, 1);
		Assert.assertEquals(energyInKWH, records.get(0).getEnergy1(), 0.0f);
	}

	@Test
	public void writeMultipleRecordsTest() throws InterruptedException {
		// write some test records
		streamWriter.putRecord(RECORD1.getDevice(), RECORD1);
		streamWriter.putRecord(RECORD1.getDevice(), RECORD1);
		streamWriter.putRecord(RECORD2.getDevice(), RECORD2);
		streamWriter.putRecord(RECORD2.getDevice(), RECORD2);
		// now we wait for the data to come through
		List<EnergyConsumptionRecordEntity> records = fetchRecordsBlocking();
		// fail if we don't have the right number records
		Assert.assertTrue(2 == records.size());
		// check that the usage is correct
		float energyInKWH = calculateEnergyInKWH(RECORD1, 1);
		Assert.assertEquals(2 * energyInKWH, records.get(0).getEnergy1(), 0.0f);
	}

	private List<EnergyConsumptionRecordEntity> fetchRecordsBlocking()
			throws InterruptedException {
		List<EnergyConsumptionRecordEntity> records = repository
				.findByDevice(DeviceEntity.TEST.DEVICEENTITY.getDevice());
		int count = 0;
		while ((0 == records.size()) && (count < 100)) {
			Thread.sleep(1000);
			records = repository.findByDevice(DeviceEntity.TEST.DEVICEENTITY
					.getDevice());
			count++;
		}
		return records;
	}

	private float calculateEnergyInKWH(RawRecordEntity record, int circuit) {
		return record.getCircuit(1).getVoltageInVolts()
				* record.getCircuit(1).getAmperageInAmps()
				* record.getDurationInSeconds() / SECONDS_PER_HOUR;
	}
}
