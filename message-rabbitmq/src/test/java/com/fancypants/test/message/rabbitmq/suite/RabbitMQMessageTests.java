package com.fancypants.test.message.rabbitmq.suite;

import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.message.rabbitmq.config.RabbitMQConfig;
import com.fancypants.test.message.rabbitmq.config.RabbitMQMessageTestConfig;
import com.fancypants.test.message.suite.MessageTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritLocations = false, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class, classes = {
		RabbitMQConfig.class, RabbitMQMessageTestConfig.class })
public class RabbitMQMessageTests extends MessageTests {

}
