package com.fancypants.test.message.sns.suite;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.message.sns.config.SNSConfig;
import com.fancypants.test.message.sns.config.SNSMessageTestConfig;
import com.fancypants.test.message.suite.MessageTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class, classes = {
		SNSConfig.class, SNSMessageTestConfig.class })
public class SNSMessageTests extends MessageTests {
	private static final Logger LOG = LoggerFactory.getLogger(SNSMessageTests.class);

	@Override
	public void after() throws Exception {
		// call base class
		super.after();
		// pause for thirty seconds to let SNS cleanup
		LOG.debug("waiting 60 seconds for SNS to cleanup");
		Thread.sleep(60 * 1000);
	}

}
