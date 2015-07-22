package com.fancypants.test.data.dynamodb.suite;

import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.data.device.dynamodb.config.DynamoDBConfig;
import com.fancypants.test.data.dynamodb.config.DynamoDBDataTestConfig;
import com.fancypants.test.data.suite.RawRecordTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritLocations = false, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class, classes = {
		DynamoDBConfig.class, DynamoDBDataTestConfig.class })
public class DynamoDBRawRecordTests extends RawRecordTests {

}
