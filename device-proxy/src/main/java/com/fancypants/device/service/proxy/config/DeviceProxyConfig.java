package com.fancypants.device.service.proxy.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class DeviceProxyConfig {

	private static final Logger LOG = LoggerFactory.getLogger(DeviceProxyConfig.class);

	@Bean
	public HttpClient httpClient() {
		// create an HttpClient
		HttpClientBuilder builder = HttpClientBuilder.create();
		HttpClient client = builder.build();
		return client;
	}

	@Bean
	@Autowired
	public HttpComponentsClientHttpRequestFactory httpClientFactory(HttpClient client) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		return factory;
	}

	@Bean
	@Autowired
	public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory, ObjectMapper objectMapper) {
		LOG.trace("restTemplate enter {}={} {}={}", "factory", factory, "objectMapper", objectMapper);
		// create the message converter
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
		jsonMessageConverter.setObjectMapper(objectMapper);
		jsonMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json,application/json"));
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(1);
		messageConverters.add(jsonMessageConverter);
		// create the REST template
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}
}
