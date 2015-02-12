package com.fancypants.test.rest.device.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.TreeSet;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLContexts;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

		// load the keystore
		KeyStore keystore = loadKeyStore(new FileSystemResource(
				arguments.keystoreFile), arguments.keystorePassword);
		// load the SSL context
		SSLContext sslContext = configureSSLContext(keystore,
				arguments.keystorePassword);
		// create the HTTP client
		HttpClient httpClient = config.adminHttpClient(sslContext);
		// create the HTTP client factory
		HttpComponentsClientHttpRequestFactory httpClientFactory = config
				.adminHttpClientFactory(httpClient);
		// create the RestTemplate
		RestTemplate restTemplate = config.adminRestTemplate(httpClientFactory);

		// create the device object
		Device device = new Device(arguments.device, arguments.serialnumber,
				new TreeSet<Circuit>(arguments.circuits));

		// do the request
		restTemplate.postForEntity(arguments.url, device, Void.class);

	}

	private static class Arguments {
		@Parameter(names = { "--device" }, required = true)
		private String device;

		@Parameter(names = { "--serial-number" }, required = true)
		private String serialnumber;

		@Parameter(names = { "--url" }, converter = URIConverter.class, required = true)
		private URI url;

		@Parameter(names = { "--keystore-file" }, converter = FileConverter.class, required = true)
		private File keystoreFile;

		@Parameter(names = { "--keystore-password" }, required = true)
		private String keystorePassword;

		@Parameter(names = { "--circuit" }, converter = CircuitConverter.class, required = true)
		private List<Circuit> circuits;

	}

	public static class CircuitConverter implements IStringConverter<Circuit> {
		@Override
		public Circuit convert(String value) {
			String[] s = value.split(":");
			Circuit circuit = new Circuit(s[0], Float.parseFloat(s[1]),
					Float.parseFloat(s[2]));
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

	private static KeyStore loadKeyStore(Resource resource, String password)
			throws NoSuchAlgorithmException, CertificateException, IOException,
			KeyStoreException {
		// create a keystore
		KeyStore keystore = KeyStore.getInstance("JKS");
		// load the file
		keystore.load(resource.getInputStream(), password.toCharArray());
		// done
		return keystore;
	}

	private static SSLContext configureSSLContext(KeyStore keystore,
			String keystorePass) throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException {
		// initialize an SSL context with the keystore
		// using all certificate in the keystore for trust material
		// and the device key as the SSL authentication certificate
		SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(keystore)
				.loadKeyMaterial(keystore, keystorePass.toCharArray()).build();
		return sslContext;
	}
}
