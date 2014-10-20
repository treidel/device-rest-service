package com.fancypants.test.rest.device.test.cases;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
	public SSLContext deviceSSLContext(
			@Value("${devicekeystore.file}") final Resource keystoreFile,
			@Value("${devicekeystore.pass}") final String keystorePass)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, KeyManagementException,
			UnrecoverableKeyException {
		// use the common internal routine
		return configureSSLContext(keystoreFile, keystorePass);
	}

	@Bean
	@Autowired
	public SSLContext adminSSLContext(
			@Value("${adminkeystore.file}") final Resource keystoreFile,
			@Value("${adminkeystore.pass}") final String keystorePass)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, KeyManagementException,
			UnrecoverableKeyException {
		// use the common internal routine
		return configureSSLContext(keystoreFile, keystorePass);
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
		RestTemplate restTemplate = new RestTemplate(factory);
		return restTemplate;
	}

	@Bean
	@Autowired
	public RestTemplate adminRestTemplate(
			@Qualifier("adminHttpClientFactory") HttpComponentsClientHttpRequestFactory factory) {
		RestTemplate restTemplate = new RestTemplate(factory);
		return restTemplate;
	}

	private SSLContext configureSSLContext(Resource keystoreFile,
			String keystorePass) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException,
			KeyManagementException, UnrecoverableKeyException {
		// create the keystore
		KeyStore keystore = KeyStore.getInstance("JKS");
		// load the keystore from the file
		keystore.load(keystoreFile.getInputStream(), keystorePass.toCharArray());

		// initialize an SSL context with the keystore
		// using all certificate in the keystore for trust material
		// and the device key as the SSL authentication certificate
		SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(keystore)
				.loadKeyMaterial(keystore, keystorePass.toCharArray()).build();
		return sslContext;
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
