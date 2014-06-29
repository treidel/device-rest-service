package com.fancypants.data.device.dynamodb.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

public class RecordIdMarshaller implements DynamoDBMarshaller<RecordId> {

	@Override
	public String marshall(RecordId getterReturnResult) {
		return getterReturnResult == null ? null : getterReturnResult
				.getDevice()
				+ "#"
				+ (getterReturnResult.getUUID() == null ? ""
						: getterReturnResult.getUUID());
	}

	@Override
	public RecordId unmarshall(Class<RecordId> clazz, String obj) {

		if (obj == null)
			return null;
		String[] parts = obj.split("#");
		RecordId deviceAndUUID = new RecordId();

		if (parts.length == 2) {
			deviceAndUUID.setDevice(parts[0]);
			deviceAndUUID.setUUID(parts[1]);
		} else {
			deviceAndUUID.setDevice(obj);

		}
		return deviceAndUUID;
	}

}
