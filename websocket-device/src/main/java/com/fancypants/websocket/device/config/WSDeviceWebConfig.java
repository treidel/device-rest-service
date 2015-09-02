package com.fancypants.websocket.device.config;

import javax.annotation.PostConstruct;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class WSDeviceWebConfig {

	public static final String KEYSTORE_ALIAS = "websocket-device";

	@Autowired
	private EmbeddedServletContainerFactory servletContainerFactory;

	@PostConstruct
	private void init() {
		// create an HTTP connector
		((TomcatEmbeddedServletContainerFactory) servletContainerFactory)
				.addAdditionalTomcatConnectors(createHTTPConnector());
	}

	public Connector createHTTPConnector() {
		Connector connector = new Connector(org.apache.coyote.http11.Http11NioProtocol.class.getName());
		connector.setPort(8003);
		connector.setScheme("http");
		return connector;
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(
			@Value("${KEYSTORE_FILE}") final Resource keystoreFile,
			@Value("${KEYSTORE_PASSWORD}") final String keystorePass) throws Exception {
		String keystoreFilePath = keystoreFile.getFile().getAbsolutePath();
		return new TomcatClientSSLAuthenticationCustomizer(keystoreFilePath, keystorePass);
	}

	private static class TomcatClientSSLAuthenticationCustomizer implements EmbeddedServletContainerCustomizer {

		private final String keystoreFilePath;
		private final String keystorePass;

		public TomcatClientSSLAuthenticationCustomizer(String keystoreFilePath, String keystorePass) {
			this.keystoreFilePath = keystoreFilePath;
			this.keystorePass = keystorePass;
		}

		@Override
		public void customize(ConfigurableEmbeddedServletContainer factory) {
			TomcatEmbeddedServletContainerFactory tomcatFactory = (TomcatEmbeddedServletContainerFactory) factory;
			tomcatFactory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
				@Override
				public void customize(Connector connector) {
					connector.setPort(8083);
					connector.setSecure(true);
					connector.setScheme("https");
					Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
					proto.setSSLEnabled(true);
					proto.setTruststoreFile(keystoreFilePath);
					proto.setTruststorePass(keystorePass);
					proto.setTruststoreType("JKS");
					proto.setKeystoreFile(keystoreFilePath);
					proto.setKeystorePass(keystorePass);
					proto.setKeystoreType("JKS");
					proto.setKeyAlias(KEYSTORE_ALIAS);
					proto.setClientAuth("true");
				}
			});
		}

	}
}
