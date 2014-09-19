package com.fancypants.data.device.dynamodb.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

public class RawRecordIdMarshaller implements DynamoDBMarshaller<RawRecordId> {

	@Override
	public String marshall(RawRecordId getterReturnResult) {
		return getterReturnResult == null ? null : getterReturnResult
				.getDevice()
				+ "#"
				+ (getterReturnResult.getUUID() == null ? ""
						: getterReturnResult.getUUID());
	}

	@Override
	public RawRecordId unmarshall(Class<RawRecordId> clazz, String obj) {

		if (obj == null)
			return null;
		String[] parts = obj.split("#");
		RawRecordId deviceAndUUID = new RawRecordId();

		if (parts.length == 2) {
			deviceAndUUID.setDevice(parts[0]);
			deviceAndUUID.setUUID(parts[1]);
		} else {
			deviceAndUUID.setDevice(obj);

		}
		return deviceAndUUID;
	}

}
