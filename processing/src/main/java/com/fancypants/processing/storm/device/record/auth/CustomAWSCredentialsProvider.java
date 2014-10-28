package com.fancypants.processing.storm.device.record.auth;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

@Component
public class CustomAWSCredentialsProvider implements AWSCredentialsProvider,
		Serializable {

	private static final long serialVersionUID = 4171449997061550552L;
	private static final Logger LOG = LoggerFactory
			.getLogger(CustomAWSCredentialsProvider.class);

	private final String accessKey;
	private final String secretKey;

	public CustomAWSCredentialsProvider() {
		this.accessKey = System.getProperty("amazon.aws.accesskey");
		this.secretKey = System.getProperty("amazon.aws.secretkey");
	}

	@Override
	public AWSCredentials getCredentials() {
		LOG.trace(this + " getCredentials() enter");
		AWSCredentials credentials = new BasicAWSCredentials(accessKey,
				secretKey);
		LOG.trace(this + " getCredentials() exit " + credentials);
		return credentials;
	}

	@Override
	public void refresh() {
		LOG.trace(this + " refresh() enter");
		// nothing to do
		LOG.trace(this + " refresh() exit");
	}

}
