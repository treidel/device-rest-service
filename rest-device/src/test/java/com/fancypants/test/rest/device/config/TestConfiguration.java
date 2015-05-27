package com.fancypants.test.rest.device.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
import com.fancypants.test.rest.security.KeyStoreCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { TestConfiguration.class,
		KeyStoreCreator.class, TestDataScanMe.class, TestMessageScanMe.class })
public class TestConfiguration {

	@Bean(name = "username")
	public String username() {
		return "admin";
	}

	@Bean(name = "password")
	public String password() {
		return "admin";
	}

	@Bean
	@Autowired
	public HttpClient httpClient(@Qualifier("username") String username,
			@Qualifier("password") String password) {
		HttpClientBuilder builder = HttpClientBuilder.create();
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("localhost", 8001),
				new UsernamePasswordCredentials(username, password));
		builder.setDefaultCredentialsProvider(credsProvider);
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
		return configureRestTemplate(factory);
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

}
