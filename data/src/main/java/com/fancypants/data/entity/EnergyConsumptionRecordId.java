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
import java.util.Date;

public class EnergyConsumptionRecordId implements
		Comparable<EnergyConsumptionRecordId>, Serializable {

	private static final long serialVersionUID = -3881765744413590343L;

	private final String device;
	private final Date date;

	public EnergyConsumptionRecordId(String device, Date date) {
		this.device = device;
		this.date = date;
	}

	@Override
	public int compareTo(EnergyConsumptionRecordId id) {
		int value = id.device.compareTo(device);
		if (0 != value) {
			return value;
		}
		return id.date.compareTo(date);
	}

	@Override
	public int hashCode() {
		return device.hashCode() + 37 * date.hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if (null == object) {
			return false;
		}
		if (false == object instanceof EnergyConsumptionRecordId) {
			return false;
		}
		EnergyConsumptionRecordId id = (EnergyConsumptionRecordId)object;
		return id.device.equals(device) && id.date.equals(date);				
	}

	public String getDevice() {
		return device;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "[EnergyConsumptionRecordId device=" + device.toString()
				+ ", date=" + date.toString() + "]";
	}

}
