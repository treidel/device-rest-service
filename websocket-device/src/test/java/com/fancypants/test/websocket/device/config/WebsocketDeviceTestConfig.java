package com.fancypants.test.websocket.device.config;

import java.security.KeyStore;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.rest.security.KeyStoreCreator;
import com.fancypants.test.stream.TestStreamScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { WebsocketDeviceTestConfig.class, TestDataScanMe.class, TestStreamScanMe.class,
		KeyStoreCreator.class })
public class WebsocketDeviceTestConfig {

	@Autowired
	@Value("${KEYSTORE_FILE}")
	private Resource keystoreFile;

	@Autowired
	private KeyStore serverKeyStore;

	@PostConstruct
	private void init() {
		serverKeyStore.toString();
	}

	@PreDestroy
	private void fini() throws Exception {
		if (true == keystoreFile.getFile().exists()) {
			keystoreFile.getFile().delete();
		}
	}

	@Bean
	@Autowired
	public WebSocketClient websocketClient(SslContextFactory sslContextFactory) throws Exception {
		// create the jetty websocket client as it understands SSL
		org.eclipse.jetty.websocket.client.WebSocketClient websocketClient = new org.eclipse.jetty.websocket.client.WebSocketClient(
				sslContextFactory);
		// start the client
		websocketClient.start();
		// create the spring wrapper class
		WebSocketClient client = new JettyWebSocketClient(websocketClient);
		return client;
	}

	@Bean
	@Autowired
	public SslContextFactory sslContextFactory(@Qualifier("deviceKeyStore") KeyStore keystore) throws Exception {
		SslContextFactory factory = new SslContextFactory();
		factory.setKeyStore(keystore);
		factory.setKeyStorePassword("device");
		factory.setTrustStore(keystore);
		factory.setTrustStorePassword("device");
		factory.start();
		return factory;
	}
}
