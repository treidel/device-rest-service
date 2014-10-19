package com.fancypants.test.rest.device.test.cases;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { TestConfiguration.class })
public class TestConfiguration {

	@Bean
	@Autowired
	public SSLContext sslContext(
			@Value("${testkeystore.file}") final Resource keystoreFile,
			@Value("${testkeystore.pass}") final String keystorePass)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, KeyManagementException,
			UnrecoverableKeyException {
		// create the keystore
		KeyStore keystore = KeyStore.getInstance("JKS");
		// load the keystore from the file
		keystore.load(keystoreFile.getInputStream(), keystorePass.toCharArray());

		// initialize an SSL context with the keystore
		SSLContext sslContext = SSLContexts.custom()
				.loadKeyMaterial(keystore, keystorePass.toCharArray()).build();
		return sslContext;
	}

	@Bean
	@Autowired
	public HttpClient httpClient(SSLContext sslContext) {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setSslcontext(sslContext);
		return builder.build();
	}

	@Bean
	@Autowired
	public HttpComponentsClientHttpRequestFactory httpClientFactory(
			HttpClient client) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		return factory;
	}

	@Bean
	@Autowired
	public RestTemplate restTemplate(
			HttpComponentsClientHttpRequestFactory factory) {
		RestTemplate restTemplate = new RestTemplate(factory);
		return restTemplate;
	}
}
