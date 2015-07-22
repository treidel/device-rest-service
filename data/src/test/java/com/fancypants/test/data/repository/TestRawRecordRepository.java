package com.fancypants.test.data.repository;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;
import com.fancypants.data.repository.RawRecordRepository;

@Component
public class TestRawRecordRepository extends
		AbstractTestRepository<RawRecordEntity, RawRecordId> implements
		RawRecordRepository {

	private static final long serialVersionUID = -989745890815966584L;

	public TestRawRecordRepository() {
		super(RawRecordEntity.class);
	}

	@Override
	public boolean insert(RawRecordEntity record) {
		RawRecordEntity existing = findOne(record.getId());
		if (null != existing) {
			return false;
		}
		save(record);
		return true;
	}
}
