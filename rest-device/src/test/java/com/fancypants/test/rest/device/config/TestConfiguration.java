package com.fancypants.test.rest.device.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.message.TestMessageScanMe;
import com.fancypants.test.rest.device.security.KeyStoreCreator;
import com.fancypants.test.stream.TestStreamScanMe;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { TestConfiguration.class,
		KeyStoreCreator.class, TestDataScanMe.class, TestMessageScanMe.class,
		TestStreamScanMe.class })
public class TestConfiguration {

	@Bean
	@Autowired
	public SSLContext deviceSSLContext(
			@Qualifier("deviceKeyStore") KeyStore keystore)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException {
		// use the common internal routine
		return configureSSLContext(keystore, "device");
	}

	@Bean
	@Autowired
	public SSLContext adminSSLContext(
			@Qualifier("adminKeyStore") KeyStore keystore)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, KeyManagementException,
			UnrecoverableKeyException {
		// use the common internal routine
		return configureSSLContext(keystore, "admin");
	}

	@Bean
	@Autowired
	public HttpClient deviceHttpClient(
			@Qualifier("deviceSSLContext") SSLContext sslContext) {
		HttpClientBuilder builder = HttpClientBuilder.create();
		// set the SSL context
		builder.setSslcontext(sslContext);
		builder.setHostnameVerifier(new CustomX509HostnameVerifier());
		return builder.build();
	}

	@Bean
	@Autowired
	public HttpClient adminHttpClient(
			@Qualifier("adminSSLContext") SSLContext sslContext) {
		HttpClientBuilder builder = HttpClientBuilder.create();
		// set the SSL context
		builder.setSslcontext(sslContext);
		builder.setHostnameVerifier(new CustomX509HostnameVerifier());
		return builder.build();
	}

	@Bean
	@Autowired
	public HttpComponentsClientHttpRequestFactory deviceHttpClientFactory(
			@Qualifier("deviceHttpClient") HttpClient client) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		return factory;
	}

	@Bean
	@Autowired
	public HttpComponentsClientHttpRequestFactory adminHttpClientFactory(
			@Qualifier("adminHttpClient") HttpClient client) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		return factory;
	}

	@Bean
	@Autowired
	public RestTemplate deviceRestTemplate(
			@Qualifier("deviceHttpClientFactory") HttpComponentsClientHttpRequestFactory factory) {
		return configureRestTemplate(factory);
	}

	@Bean
	@Autowired
	public RestTemplate adminRestTemplate(
			@Qualifier("adminHttpClientFactory") HttpComponentsClientHttpRequestFactory factory) {
		return configureRestTemplate(factory);
	}

	private SSLContext configureSSLContext(KeyStore keystore,
			String keystorePass) throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException {
		// initialize an SSL context with the keystore
		// using all certificate in the keystore for trust material
		// and the device key as the SSL authentication certificate
		SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(keystore)
				.loadKeyMaterial(keystore, keystorePass.toCharArray()).build();
		return sslContext;
	}

	private RestTemplate configureRestTemplate(
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

}
