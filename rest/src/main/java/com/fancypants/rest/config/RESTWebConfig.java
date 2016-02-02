package com.fancypants.rest.config;

import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import ch.qos.logback.access.tomcat.LogbackValve;

@Configuration
public class RESTWebConfig {

	private static final Logger LOG = LoggerFactory.getLogger(RESTWebConfig.class);

	private static final String LOGBACK_ACCESS_FILE = "logback-access.xml";

	@Autowired
	private MappingJackson2HttpMessageConverter httpMessageConverter;

	@PostConstruct
	public void init() throws Exception {
		LOG.trace("init enter");
		// tweak the serialization of dates
		httpMessageConverter.getObjectMapper().setDateFormat(new ISO8601DateFormat());

		LOG.trace("init exit");
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() throws Exception {
		LOG.trace("servletContainer enter");
		// create the tomcat container factory
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		// resolve the embedded config file
		Resource resource = new ClassPathResource(LOGBACK_ACCESS_FILE);
		if (false == resource.exists()) {
			throw new IllegalStateException("could not find access log config file: " + LOGBACK_ACCESS_FILE);
		}
		LOG.debug("found access log config file: {}", resource);

		// create the temporary config file
		File configFile = File.createTempFile("temp", ".xml");
		configFile.deleteOnExit();

		LOG.debug("copying {} to {}", resource, configFile);
		FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(configFile));

		// create the access logging valve
		LogbackValve valve = new LogbackValve();
		// configure the valve
		valve.setFilename(configFile.getAbsolutePath());
		// add the valve
		tomcat.addContextValves(valve);

		LOG.trace("servletContainer exit {}", tomcat);
		return tomcat;
	}

}
