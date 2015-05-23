package com.fancypants.data.device.dynamodb.credentials;

import java.io.Serializable;

import com.amazonaws.auth.AWSCredentials;

public class SerializableCredentials implements AWSCredentials, Serializable {

	private static final long serialVersionUID = 3399655685045290247L;

	private final String awsAccessKeyId;
	private final String awsSecretKey;

	public SerializableCredentials(String awsAccessKeyId, String awsSecretKey) {
		this.awsAccessKeyId = awsAccessKeyId;
		this.awsSecretKey = awsSecretKey;
	}

	@Override
	public String getAWSAccessKeyId() {
		return awsAccessKeyId;
	}

	@Override
	public String getAWSSecretKey() {
		return awsSecretKey;
	}

}
