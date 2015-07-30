package com.fancypants.jobs.instances;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fancypants.data.partitioner.Partition;
import com.fancypants.data.partitioner.RawRecordPartitioner;
import com.fancypants.data.repository.RawRecordRepository;

@Component
@EnableScheduling
public class RawRecordPartitionCreatorJob {

	private static final Logger LOG = LoggerFactory.getLogger(RawRecordPartitionCreatorJob.class);
	private static final long SCHEDULED_RATE = 5 * 60 * 1000; // 5 minutes

	@Autowired
	private RawRecordRepository repository;

	@Autowired
	private RawRecordPartitioner partitioner;

	@Scheduled(fixedRate = SCHEDULED_RATE, initialDelay = 1000)
	public void run() {
		LOG.trace("run enter");

		LOG.debug("job running");
		// get the current partition
		Date currentTime = new Date();
		Partition currentPartition = partitioner.partitionByValue(currentTime);

		// make sure the partition exists
		repository.createPartition(currentPartition);

		// look 10 minutes into the future and pre-create the partition
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTime(currentTime);
		calendar.add(Calendar.MINUTE, 10);
		Date futureTime = calendar.getTime();
		Partition nextPartition = partitioner.partitionByValue(futureTime);

		// if this would be a different partition create it
		if (false == nextPartition.equals(currentPartition)) {
			// make sure the partition exists or create it if not
			repository.createPartition(nextPartition);
		}

		LOG.trace("run exit");
	}
}
