package com.fancypants.test.processing.storm.device.record.test.cases;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.processing.storm.device.record.RawProcessor;
import com.fancypants.stream.writer.StreamWriter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { RawProcessor.class,
		TestConfiguration.class })
@IntegrationTest
public class ProcessingTests {

	@Autowired
	private StreamWriter<RawRecordEntity> streamWriter;

	@Autowired
	private HourlyRecordRepository repository;

	@Before
	public void cleanup() {
		repository.deleteAllForDevice(DeviceEntity.TEST.DEVICEENTITY
				.getDevice());
	}

	@Test
	public void writeRecordsTest() throws InterruptedException {
		// write some test records
		for (RawRecordEntity record : RawRecordEntity.TEST.RECORDS) {
			streamWriter.putRecord(record.getDevice(), record);
		}
		// now we wait for the data to come through
		List<EnergyConsumptionRecordEntity> records = repository
				.findByDevice(DeviceEntity.TEST.DEVICEENTITY.getDevice());
		int count = 0;
		while ((0 == records.size()) && (count < 10)) {
			Thread.sleep(1000);
			records = repository.findByDevice(DeviceEntity.TEST.DEVICEENTITY
					.getDevice());
			count++;
		}
		// fail if we don't have records
		Assert.assertTrue(0 != records.size());
	}
}
