package com.fancypants.data.partitioner;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.common.generators.DateIntervalGenerator;
import com.fancypants.common.generators.HourlyDateIntervalGenerator;
import com.fancypants.data.entity.RawRecordEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RawRecordPartitioner implements Partitioner<RawRecordEntity, Date>, Serializable {

	private static final long serialVersionUID = 2811693714166814268L;
	private static final Logger LOG = LoggerFactory.getLogger(RawRecordPartitioner.class);
	private static final DateIntervalGenerator GENERATOR = new HourlyDateIntervalGenerator();

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Partition partitionByEntity(RawRecordEntity entity) {
		LOG.trace("partitionByEntity enter", "entity", entity);
		// calculate the start date of the partition
		Date startDate = GENERATOR.flattenDate(entity.getTimestamp());
		// calculate the partition
		String tag = objectMapper.getDeserializationConfig().getDateFormat().format(startDate);
		Partition partition = new Partition(tag);
		LOG.trace("partitionByEntity exit", partition);
		return partition;
	}

	@Override
	public Partition partitionByValue(Date value) {
		LOG.trace("partitionByValue enter", "value", value);
		// calculate the start date of the partition
		Date startDate = GENERATOR.flattenDate(value);
		// calculate the partition
		String tag = objectMapper.getDeserializationConfig().getDateFormat().format(startDate);
		Partition partition = new Partition(tag);
		LOG.trace("partitionByValue exit", partition);
		return partition;
	}

}
