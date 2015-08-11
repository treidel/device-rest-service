package com.fancypants.jobs.instances;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fancypants.data.partitioner.Partition;
import com.fancypants.data.repository.RawRecordRepository;

@Component
@EnableScheduling
public class RawRecordPartitionDeleteJob {

	private static final Logger LOG = LoggerFactory.getLogger(RawRecordPartitionDeleteJob.class);
	private static final long SCHEDULED_RATE = 10 * 60 * 1000; // 10 minutes
	private static final int NUM_PARTITIONS = 2;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("y-M-d");

	static {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Autowired
	private RawRecordRepository repository;

	@Scheduled(fixedRate = SCHEDULED_RATE, initialDelay = 1000)
	public void run() throws Exception {
		LOG.trace("run enter");

		LOG.debug("job running");

		// calculate the earliest date that we will keep using the GMT timezone
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, -1 * NUM_PARTITIONS);
		Date earliest = calendar.getTime();

		// get a list of all partitions
		List<Partition> partitions = repository.listPartitions();
		for (Partition partition : partitions) {
			// parse the date associated with the partition
			Date date = DATE_FORMAT.parse(partition.getValue());
			// see if this partition is too old
			if (earliest.getTime() > date.getTime()) {
				// TBD: invoke hive to backup table data

				LOG.info("purging raw record partition={}", partition);
				repository.deletePartition(partition);
			}
		}

		LOG.trace("run exit");
	}
}
