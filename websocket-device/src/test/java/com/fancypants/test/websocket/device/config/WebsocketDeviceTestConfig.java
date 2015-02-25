package com.fancypants.test.websocket.device.config;

import java.security.KeyStore;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.rest.security.KeyStoreCreator;
import com.fancypants.test.stream.TestStreamScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { WebsocketDeviceTestConfig.class,
		TestDataScanMe.class, TestStreamScanMe.class, KeyStoreCreator.class })
public class WebsocketDeviceTestConfig {

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

	/*
	@Bean
	public RestTemplate restTemplate(
			HttpComponentsClientHttpRequestFactory factory) {
		RestTemplate restTemplate = new RestTemplate(factory);
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
		jsonMessageConverter.setObjectMapper(createObjectMapper());
		jsonMessageConverter.setSupportedMediaTypes(MediaType
				.parseMediaTypes("application/hal+json,application/json"));
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(
				1);
		messageConverters.add(jsonMessageConverter);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}

	@Bean
	@Autowired
	public HttpClient deviceHttpClient(SSLContext sslContext) {
		HttpClientBuilder builder = HttpClientBuilder.create();
		// set the SSL context
		builder.setSslcontext(sslContext);
		builder.setHostnameVerifier(new CustomX509HostnameVerifier());
		return builder.build();
	}

	@Bean
	public SSLContext sslContext(@Qualifier("deviceKeyStore") KeyStore keystore)
			throws Exception {
		// initialize an SSL context with the keystore
		// using all certificate in the keystore for trust material
		// and the device key as the SSL authentication certificate
		SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(keystore)
				.loadKeyMaterial(keystore, "device".toCharArray()).build();
		return sslContext;
	}

	@Bean
	@Autowired
	public HttpComponentsClientHttpRequestFactory httpClientFactory(
			HttpClient client) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		return factory;
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setDateFormat(new ISO8601DateFormat());
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.registerModule(new Jackson2HalModule());
		return objectMapper;
	}

	private static class CustomX509HostnameVerifier implements
			X509HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

		@Override
		public void verify(String host, String[] cns, String[] subjectAlts)
				throws SSLException {
		}

		@Override
		public void verify(String host, X509Certificate cert)
				throws SSLException {
		}

		@Override
		public void verify(String host, SSLSocket ssl) throws IOException {

		}
	};
*/
	@Bean
	@Autowired
	public SslContextFactory sslContextFactory(
			@Qualifier("deviceKeyStore") KeyStore keystore) throws Exception {
		SslContextFactory factory = new SslContextFactory();
		factory.setKeyStore(keystore);
		factory.setKeyStorePassword("device");
		factory.setTrustStore(keystore);
		factory.setTrustStorePassword("device");
		factory.start();
		return factory;
	}
}
