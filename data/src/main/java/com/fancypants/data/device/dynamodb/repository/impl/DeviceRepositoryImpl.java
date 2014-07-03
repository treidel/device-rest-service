package com.fancypants.data.device.dynamodb.repository.impl;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBEntityInformation;
import org.socialsignin.spring.data.dynamodb.repository.support.EnableScanAnnotationPermissions;
import org.socialsignin.spring.data.dynamodb.repository.support.SimpleDynamoDBPagingAndSortingRepository;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;

@Component
public class DeviceRepositoryImpl extends
		SimpleDynamoDBPagingAndSortingRepository<DeviceEntity, String>
		implements DeviceRepository {

	public DeviceRepositoryImpl(DynamoDBOperations dynamoDBOperations,
			DynamoDBEntityInformation<DeviceEntity, String> entityInformation) {
		super(entityInformation, dynamoDBOperations,
				new EnableScanAnnotationPermissions(DeviceRepository.class));
	}

}
