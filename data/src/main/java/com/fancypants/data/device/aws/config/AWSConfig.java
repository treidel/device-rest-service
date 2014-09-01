package com.fancypants.data.device.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

@Configuration
public class AWSConfig {

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(getAmazonAWSAccessKey(),
				getAmazonAWSSecretKey());
	}

	private String getAmazonAWSAccessKey() {
		return System.getProperty("amazon.aws.accesskey");
	}

	private String getAmazonAWSSecretKey() {
		return System.getProperty("amazon.aws.secretkey");
	}

}
