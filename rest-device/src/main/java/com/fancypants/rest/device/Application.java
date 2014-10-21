package com.fancypants.rest.device;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import com.fancypants.data.device.DataDeviceScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.records.RecordsScanMe;
import com.fancypants.rest.RestScanMe;
import com.fancypants.stream.StreamScanMe;
import com.fancypants.stream.kinesis.StreamKinesisScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { RestScanMe.class, RestDeviceScanMe.class,
		StreamScanMe.class, StreamKinesisScanMe.class, DataDeviceScanMe.class,
		DataDeviceDynamoDBScanMe.class, DeviceScanMe.class, RecordsScanMe.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(
			@Value("${keystore.file}") final Resource keystoreFile,
			@Value("${keystore.pass}") final String keystorePass)
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
							connector.setPort(8443);
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
