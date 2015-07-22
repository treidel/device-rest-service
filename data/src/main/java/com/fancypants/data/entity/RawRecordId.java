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

import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = RawRecordId.Serializer.class)
@JsonDeserialize(using = RawRecordId.Deserializer.class)
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

	public static final class Serializer extends JsonSerializer<RawRecordId> {
		@Override
		public void serialize(RawRecordId value, JsonGenerator jgen,
				SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeString(value.getDevice() + ":"
					+ value.getUUID().toString());
		}
	}

	public static final class Deserializer extends
			JsonDeserializer<RawRecordId> {
		@Override
		public RawRecordId deserialize(JsonParser jp,
				DeserializationContext ctxt) throws IOException,
				JsonProcessingException {
			String raw = jp.getText();
			StringTokenizer tokenizer = new StringTokenizer(raw, ":");
			String device = tokenizer.nextToken();
			String uuid = tokenizer.nextToken();
			RawRecordId id = new RawRecordId(device, UUID.fromString(uuid));
			return id;
		}
	}
}
