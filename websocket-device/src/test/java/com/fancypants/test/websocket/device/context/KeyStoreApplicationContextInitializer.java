package com.fancypants.test.websocket.device.context;

import java.io.File;
import java.io.IOException;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.util.Assert;

public class KeyStoreApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final String TEST_KEYSTORE_PASSWORD = "server";

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		try {
			// create the file
			File keystoreFile = File.createTempFile(getClass().getCanonicalName(), ".jks");
			// create a mock property source
			MockPropertySource props = new MockPropertySource();
			props.setProperty("KEYSTORE_FILE", "file://" + keystoreFile.getAbsolutePath());
			props.setProperty("KEYSTORE_PASSWORD", TEST_KEYSTORE_PASSWORD);
			// get the environment
			ConfigurableEnvironment environment = applicationContext.getEnvironment();
			// add it to the environment
			environment.getPropertySources().addFirst(props);
		} catch (IOException e) {
			Assert.isTrue(false);
		}
	}
}
