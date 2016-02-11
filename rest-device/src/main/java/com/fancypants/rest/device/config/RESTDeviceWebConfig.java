package com.fancypants.rest.device.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.CommonScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.rest.RestScanMe;
import com.fancypants.rest.device.RestDeviceScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, RestScanMe.class, RestDeviceScanMe.class,
		DeviceScanMe.class })
public class RESTDeviceWebConfig {
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() throws Exception {
		return new TomcatCustomizer();
	}

	private static class TomcatCustomizer implements EmbeddedServletContainerCustomizer {

		@Override
		public void customize(ConfigurableEmbeddedServletContainer factory) {
			TomcatEmbeddedServletContainerFactory tomcatFactory = (TomcatEmbeddedServletContainerFactory) factory;
			tomcatFactory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
				@Override
				public void customize(Connector connector) {
					connector.setPort(8001);
				}
			});
		}

	}
}
