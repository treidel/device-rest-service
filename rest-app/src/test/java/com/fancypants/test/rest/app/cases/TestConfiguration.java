package com.fancypants.test.rest.app.cases;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.fancypants.test.data.values.DeviceValues;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { TestConfiguration.class,
		TestDataScanMe.class })
public class TestConfiguration {

	@Bean
	public HttpClient httpClient() {
		// create an HttpClient
		HttpClientBuilder builder = HttpClientBuilder.create();
		CredentialsProvider provider = new BasicCredentialsProvider();
		Credentials credentials = new UsernamePasswordCredentials(
				DeviceValues.DEVICEENTITY.getDevice(),
				DeviceValues.DEVICEENTITY.getSerialNumber());
		provider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = builder.setDefaultCredentialsProvider(provider)
				.build();
		return client;
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
		// create the object mapper
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setDateFormat(new ISO8601DateFormat());
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.registerModule(new Jackson2HalModule());
		// create the message converter
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
		jsonMessageConverter.setObjectMapper(objectMapper);
		jsonMessageConverter.setSupportedMediaTypes(MediaType
				.parseMediaTypes("application/hal+json,application/json"));
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(
				1);
		messageConverters.add(jsonMessageConverter);
		// create the REST template
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}
}
