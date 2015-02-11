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
package com.fancypants.data.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite id for the Thread entity. For spring-data-dynamodb to be able to
 * identify which attribute is the hash key and which is the range key the
 * methods must be annotated with @DynamoDBHashKey or @DynamoDBRangeKey
 * 
 */
public class RawRecordId implements Comparable<RawRecordId>, Serializable {

	private static final long serialVersionUID = 2392535614010208931L;

	private final String device;
	private final UUID uuid;

	public RawRecordId(String device, UUID uuid) {
		this.device = device;
		this.uuid = uuid;
	}

	@Override
	public int compareTo(RawRecordId id) {
		int value = id.device.compareTo(device);
		if (0 != value) {
			return value;
		}
		return id.uuid.compareTo(id.uuid);
	}

	@Override
	public int hashCode() {
		return device.hashCode() * 31 + uuid.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (null == object) {
			return false;
		}
		if (false == object instanceof RawRecordId) {
			return false;
		}
		RawRecordId id = (RawRecordId) object;
		return id.device.equals(device) && id.uuid.equals(uuid);
	}

	public String getDevice() {
		return device;
	}

	public UUID getUUID() {
		return uuid;
	}

	@Override
	public String toString() {
		return "[RawRecordId device=" + device.toString() + ", uuid="
				+ uuid.toString() + "]";
	}
}
