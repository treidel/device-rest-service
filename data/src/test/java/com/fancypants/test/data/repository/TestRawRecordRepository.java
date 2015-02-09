package com.fancypants.test.data.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.entity.RawRecordId;
import com.fancypants.data.device.repository.RawRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRawRecordRepository extends
		AbstractTestRepository<RawRecordId, RawRecordEntity> implements
		RawRecordRepository {

	private static final long serialVersionUID = -989745890815966584L;

	private final Map<String, Map<UUID, RawRecordEntity>> devices = new HashMap<String, Map<UUID, RawRecordEntity>>();

	public TestRawRecordRepository(ObjectMapper mapper) {
		super(mapper, RawRecordEntity.class);
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

	@Override
	public <S extends RawRecordEntity> S save(S entity) {
		super.save(entity);
		Map<UUID, RawRecordEntity> records = devices.get(entity.getDevice());
		if (null == records) {
			records = new TreeMap<UUID, RawRecordEntity>();
			devices.put(entity.getDevice(), records);
		}
		records.put(UUID.fromString(entity.getUUID()), entity);
		return entity;
	}

	@Override
	public List<RawRecordEntity> findAllForDevice(String device) {
		Map<UUID, RawRecordEntity> records = devices.get(device);
		if (null == records) {
			return Arrays.asList(new RawRecordEntity[0]);
		}
		return new ArrayList<RawRecordEntity>(records.values());
	}

	@Override
	public void deleteAllForDevice(String device) {
		Map<UUID, RawRecordEntity> records = devices.remove(device);
		if (null != records) {
			for (RawRecordEntity entity : records.values()) {
				delete(entity);
			}
		}
	}

}
