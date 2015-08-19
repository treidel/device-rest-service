package com.fancypants.rest.config;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import ch.qos.logback.access.tomcat.LogbackValve;

@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class RESTWebConfig {

	private static final Logger LOG = LoggerFactory.getLogger(RESTWebConfig.class);

	private static final String LOGBACK_ACCESS_FILE = "logback-access.xml";

	private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	@Autowired
	private MappingJackson2HttpMessageConverter httpMessageConverter;

	@PostConstruct
	public void init() {
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
		// find the config file
		File configFile = null;
		Resource resources[] = resolver.getResources("classpath*:/" + LOGBACK_ACCESS_FILE);
		switch (resources.length) {
		case 0:
			LOG.warn("could not find access log config file: {}", LOGBACK_ACCESS_FILE);
			break;
		case 1:
			LOG.info("found access log config file: {}", resources[0]);
			configFile = resources[0].getFile();
			break;

		default:
			LOG.warn("found multiple " + LOGBACK_ACCESS_FILE + " files, using first found: {}", resources[0]);
			configFile = resources[0].getFile();
			break;
		}

		// add the access logging valve to the tomcat config if we found a
		// config file
		if (null != configFile) {
			// create the access logging valve
			LogbackValve valve = new LogbackValve();
			// configure the valve
			valve.setFilename(configFile.getAbsolutePath());
			// add the valve
			tomcat.addContextValves(valve);
		}

		// disable access logging
		LOG.trace("servletContainer exit {}", tomcat);
		return tomcat;
	}

}
