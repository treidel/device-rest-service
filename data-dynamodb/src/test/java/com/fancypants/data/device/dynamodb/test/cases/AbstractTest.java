package com.fancypants.data.device.dynamodb.test.cases;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.data.device.dynamodb.config.DynamoDBConfig;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DynamoDBConfig.class)
public class AbstractTest {

}
