/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fancypants.data.device.dynamodb.repository;

import org.socialsignin.spring.data.dynamodb.repository.DynamoDBPagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;

public interface RecordRepository extends
		DynamoDBPagingAndSortingRepository<RecordEntity, RecordId> {

	public Page<RecordEntity> findByDevice(String device, Pageable pageable);

	public Page<RecordEntity> findByDeviceAndTimestampRange(
			String timestamp_low, String timestamp_high);

}
