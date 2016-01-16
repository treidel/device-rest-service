package com.fancypants.message.sns.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.fancypants.common.CommonScanMe;
import com.fancypants.message.MessageScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, MessageScanMe.class })
public class SNSConfig {

	private static final String AMAZON_ACCESS_KEY_ID_ENVVAR = "AWS_ACCESS_KEY_ID";
	private static final String AMAZON_SECRET_ACCESS_KEY_ENVVAR = "AWS_SECRET_ACCESS_KEY";
	private static final String AMAZON_REGION_ENVVAR = "AWS_REGION";

	private static final Logger LOG = LoggerFactory.getLogger(SNSConfig.class);

	@Bean
	public Region awsRegion(@Value("${" + AMAZON_REGION_ENVVAR + "}") String awsRegion) {
		LOG.trace("awsRegion enter {}={}", "awsRegion", awsRegion);
		Region region = Region.getRegion(Regions.fromName(awsRegion));
		LOG.trace("awsRegion exit {}", region);
		return region;
	}

	@Bean
	@Autowired
	public AmazonSNSClient amazonSNSClient(@Value("${" + AMAZON_ACCESS_KEY_ID_ENVVAR + "}") String accessKeyId,
			@Value("${" + AMAZON_SECRET_ACCESS_KEY_ENVVAR + "}") String secretAccessKey, Region awsRegion) {
		LOG.trace("amazonSNSClient enter {}={} {}={} {}={}", "accessKeyId", accessKeyId, "secretAcessKey",
				secretAccessKey, "awsRegion", awsRegion);
		// create the credentials
		AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		// create the SNS client
		AmazonSNSClient client = new AmazonSNSClient(credentials).withRegion(awsRegion);
		LOG.trace("amazonSNSClient exit {}", client);
		return client;
	}

	@Bean
	@Autowired
	public AmazonSQSClient amazonSQSClient(@Value("${" + AMAZON_ACCESS_KEY_ID_ENVVAR + "}") String accessKeyId,
			@Value("${" + AMAZON_SECRET_ACCESS_KEY_ENVVAR + "}") String secretAccessKey, Region awsRegion) {
		LOG.trace("AmazonSQSClient enter {}={} {}={} {}={}", "accessKeyId", accessKeyId, "secretAcessKey",
				secretAccessKey, "awsRegion", awsRegion);
		// create the credentials
		AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		// create the SQS client
		AmazonSQSClient client = new AmazonSQSClient(credentials).withRegion(awsRegion);
		LOG.trace("AmazonSQSClient exit {}", client);
		return client;
	}

	@Bean
	public String amazonAccountIdentifier(@Value("${" + AMAZON_ACCESS_KEY_ID_ENVVAR + "}") String accessKeyId,
			@Value("${" + AMAZON_SECRET_ACCESS_KEY_ENVVAR + "}") String secretAccessKey) {
		LOG.trace("amazonAccountIdentifier enter {}={} {}={}", "accessKeyId", accessKeyId, "secretAccessKey",
				secretAccessKey);
		// create the credentials
		AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		AmazonIdentityManagementClient amazonIdentityClient = new AmazonIdentityManagementClient(credentials);
		GetUserResult result = amazonIdentityClient.getUser();
		String arn = result.getUser().getArn();
		String parts[] = arn.split(":");
		String accountId = parts[4];
		LOG.trace("amazonAccountIdentifier exit {}", accountId);
		return accountId;
	}
}
