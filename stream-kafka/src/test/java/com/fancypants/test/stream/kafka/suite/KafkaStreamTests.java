package com.fancypants.test.stream.kafka.suite;

import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.test.stream.kafka.config.KafkaStreamTestConfig;
import com.fancypants.test.stream.suite.StreamTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, loader = AnnotationConfigContextLoader.class, classes = KafkaStreamTestConfig.class, initializers = ConfigFileApplicationContextInitializer.class)
public class KafkaStreamTests extends StreamTests {


}
