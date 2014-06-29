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
package com.fancypants.data.device.dynamodb.entity;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

/**
 * Composite id for the Thread entity. For spring-data-dynamodb to be able to
 * identify which attribute is the hash key and which is the range key the
 * methods must be annotated with @DynamoDBHashKey or @DynamoDBRangeKey
 * 
 */
public class RecordId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String device;
	private String uuid;

	@DynamoDBHashKey
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	@DynamoDBRangeKey
	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

}
