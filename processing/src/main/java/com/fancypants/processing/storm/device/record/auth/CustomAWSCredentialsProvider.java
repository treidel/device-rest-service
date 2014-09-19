package com.fancypants.processing.storm.device.record.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

public class CustomAWSCredentialsProvider implements AWSCredentialsProvider {

	private static final Logger LOG = LoggerFactory.getLogger(CustomAWSCredentialsProvider.class);
	
	@Override
	public AWSCredentials getCredentials() {
		LOG.trace(this + " getCredentials() enter");
		AWSCredentials credentials = new BasicAWSCredentials(getAmazonAWSAccessKey(),
				getAmazonAWSSecretKey());
		LOG.trace(this + " getCredentials() exit " + credentials);
		return credentials;
	}

	@Override
	public void refresh() {
		LOG.trace(this + " refresh() enter");
		// nothing to do 
		LOG.trace(this + " refresh() exit");
	}
	
	private String getAmazonAWSAccessKey() {
		return System.getProperty("amazon.aws.accesskey");
	}

	private String getAmazonAWSSecretKey() {
		return System.getProperty("amazon.aws.secretkey");
	}

}
