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
package com.fancypants.data.device.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite id for the Thread entity. For spring-data-dynamodb to be able to
 * identify which attribute is the hash key and which is the range key the
 * methods must be annotated with @DynamoDBHashKey or @DynamoDBRangeKey
 * 
 */
public class RawRecordId implements Serializable {

	private static final long serialVersionUID = 2392535614010208931L;
	
	private final String device;
	private final UUID uuid;

	public RawRecordId(String device, UUID uuid) {
		this.device = device;
		this.uuid = uuid;
	}

	public String getDevice() {
		return device;
	}

	public UUID getUUID() {
		return uuid;
	}

}
