package com.fancypants.data.device.dynamodb.entity;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "records")
public class RecordEntity {

	private String timestamp;

	@Id
	@DynamoDBMarshalling(marshallerClass = RecordIdMarshaller.class)
	private RecordId recordId;

	public RecordId getRecordId() {
		return recordId;
	}

	public void setRecordId(RecordId recordId) {
		this.recordId = recordId;
	}

	@DynamoDBHashKey(attributeName = "device")
	public String getDevice() {
		return recordId != null ? recordId.getDevice() : null;
	}

	public void setDevice(String device) {
		if (recordId == null) {
			recordId = new RecordId();
		}
		this.recordId.setDevice(device);
	}

	@DynamoDBRangeKey(attributeName = "uuid")
	public String getUUID() {
		return recordId != null ? recordId.getUUID() : null;
	}

	public void setUUID(String uuid) {
		if (recordId == null) {
			recordId = new RecordId();
		}
		this.recordId.setUUID(uuid);

	}

	@DynamoDBIndexRangeKey(attributeName = "timestamp")
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
