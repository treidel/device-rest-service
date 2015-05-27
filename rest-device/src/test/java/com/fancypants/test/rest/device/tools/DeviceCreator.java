package com.fancypants.test.rest.device.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.TreeSet;

import org.apache.http.client.HttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fancypants.rest.domain.Circuit;
import com.fancypants.rest.domain.Device;
import com.fancypants.test.rest.device.config.TestConfiguration;

public class DeviceCreator {

	public static void main(String[] args) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException,
			UnrecoverableKeyException {
		// parse arguments
		Arguments arguments = new Arguments();
		new JCommander(arguments, args);

		// reuse the test configuration helper
		TestConfiguration config = new TestConfiguration();

		// create the HTTP client
		HttpClient httpClient = config.httpClient(arguments.username,
				arguments.password);
		// create the HTTP client factory
		HttpComponentsClientHttpRequestFactory httpClientFactory = config
				.httpClientFactory(httpClient);
		// create the RestTemplate
		RestTemplate restTemplate = config.restTemplate(httpClientFactory);

		// create the device object
		Device device = new Device(arguments.device, arguments.serialnumber,
				new TreeSet<Circuit>(arguments.circuits));

		// do the request
		restTemplate.postForEntity(arguments.url, device, Void.class);

	}

	private static class Arguments {
		@Parameter(names = { "--username" }, required = true)
		private String username;

		@Parameter(names = { "--password" }, required = true)
		private String password;

		@Parameter(names = { "--device" }, required = true)
		private String device;

		@Parameter(names = { "--serial-number" }, required = true)
		private String serialnumber;

		@Parameter(names = { "--url" }, converter = URIConverter.class, required = true)
		private URI url;

		@Parameter(names = { "--circuit" }, converter = CircuitConverter.class, required = true)
		private List<Circuit> circuits;

	}

	public static class CircuitConverter implements IStringConverter<Circuit> {
		@Override
		public Circuit convert(String value) {
			String[] s = value.split(":");
			Circuit circuit = new Circuit(s[0], Float.parseFloat(s[1]));
			return circuit;
		}
	}

	public static class FileConverter implements IStringConverter<File> {
		@Override
		public File convert(String value) {
			return new File(value);
		}
	}

	public static class URIConverter implements IStringConverter<URI> {
		@Override
		public URI convert(String value) {
			return URI.create(value);
		}
	}
}
