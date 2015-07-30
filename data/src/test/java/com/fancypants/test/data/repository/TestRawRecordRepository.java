package com.fancypants.test.data.repository;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;
import com.fancypants.data.partitioner.Partition;
import com.fancypants.data.partitioner.RawRecordPartitioner;
import com.fancypants.data.repository.RawRecordRepository;

@Component
public class TestRawRecordRepository extends PartitionedTestRepository<RawRecordEntity, RawRecordId, Date>
		implements RawRecordRepository {

	private static final long serialVersionUID = -989745890815966584L;

	@Autowired
	public TestRawRecordRepository(RawRecordPartitioner partitioner) {
		super(RawRecordEntity.class, partitioner);
	}

	@Override
	public boolean insert(RawRecordEntity record) {
		// calculate the partition
		Partition partition = getPartitioner().partitionByEntity(record);
		// get the partition
		CrudRepository<RawRecordEntity, RawRecordId> partitionTable = retrievePartitionTable(partition);
		// lookup the record
		RawRecordEntity existing = partitionTable.findOne(record.getId());
		if (null != existing) {
			return false;
		}
		// save the record in the partition directly
		partitionTable.save(record);
		return true;
	}
}
