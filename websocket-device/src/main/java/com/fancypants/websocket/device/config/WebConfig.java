package com.fancypants.websocket.device.config;

import java.io.File;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import ch.qos.logback.access.tomcat.LogbackValve;

@Configuration
public class WebConfig {
	private static final Logger LOG = LoggerFactory.getLogger(WebConfig.class);
	private static final String LOGBACK_ACCESS_FILE = "logback-access.xml";

	private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	@Bean
	public EmbeddedServletContainerFactory servletContainer() throws Exception {
		LOG.trace("servletContainer enter");
		// create the tomcat container factory
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		// find the config file
		File configFile = null;
		Resource resources[] = resolver.getResources("classpath*:/"
				+ LOGBACK_ACCESS_FILE);
		switch (resources.length) {
		case 0:
			LOG.warn("could not find access log config file: {}",
					LOGBACK_ACCESS_FILE);
			break;
		case 1:
			LOG.info("found access log config file: {}", resources[0]);
			configFile = resources[0].getFile();
			break;

		default:
			LOG.warn("found multiple " + LOGBACK_ACCESS_FILE
					+ " files, using first found: {}", resources[0]);
			configFile = resources[0].getFile();
			break;
		}

		// add the access logging valve to the tomcat config if we found a config file
		if (null != configFile) {
			// create the access logging valve
			LogbackValve valve = new LogbackValve();
			// configure the valve
			valve.setFilename(configFile.getAbsolutePath());
			// add the vavle
			tomcat.addContextValves(valve);
		}
		// create an HTTP connector
		tomcat.addAdditionalTomcatConnectors(createHTTPConnector());
		// disable access logging 
		LOG.trace("servletContainer exit {}", tomcat);
		return tomcat;
	}

	public Connector createHTTPConnector() {
		Connector connector = new Connector(
				org.apache.coyote.http11.Http11NioProtocol.class.getName());
		connector.setPort(8003);
		connector.setScheme("http");
		return connector;
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(
			@Value("${KEYSTORE_FILE}") final Resource keystoreFile,
			@Value("${KEYSTORE_PASSWORD}") final String keystorePass)
			throws Exception {
		String keystoreFilePath = keystoreFile.getFile().getAbsolutePath();
		return new TomcatClientSSLAuthenticationCustomizer(keystoreFilePath,
				keystorePass);
	}

	private static class TomcatClientSSLAuthenticationCustomizer implements
			EmbeddedServletContainerCustomizer {

		private final String keystoreFilePath;
		private final String keystorePass;

		public TomcatClientSSLAuthenticationCustomizer(String keystoreFilePath,
				String keystorePass) {
			this.keystoreFilePath = keystoreFilePath;
			this.keystorePass = keystorePass;
		}

		@Override
		public void customize(ConfigurableEmbeddedServletContainer factory) {
			TomcatEmbeddedServletContainerFactory tomcatFactory = (TomcatEmbeddedServletContainerFactory) factory;
			tomcatFactory
					.addConnectorCustomizers(new TomcatConnectorCustomizer() {
						@Override
						public void customize(Connector connector) {
							connector.setPort(8083);
							connector.setSecure(true);
							connector.setScheme("https");
							Http11NioProtocol proto = (Http11NioProtocol) connector
									.getProtocolHandler();
							proto.setSSLEnabled(true);
							proto.setTruststoreFile(keystoreFilePath);
							proto.setTruststorePass(keystorePass);
							proto.setTruststoreType("JKS");
							proto.setKeystoreFile(keystoreFilePath);
							proto.setKeystorePass(keystorePass);
							proto.setKeystoreType("JKS");
							proto.setKeyAlias("server");
							proto.setClientAuth("true");
						}
					});
		}

	}
}
