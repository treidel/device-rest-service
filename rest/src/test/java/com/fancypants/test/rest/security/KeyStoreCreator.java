package com.fancypants.test.rest.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import sun.security.x509.CertAndKeyGen;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import com.fancypants.test.data.values.DeviceValues;

@Component
public class KeyStoreCreator {

	private static final String KEYGEN_ALGORITHM = "SHA256WithRSA";
	private static final String ROOT_X500_NAME = "CN=test.fancypants.com, EMAILADDRESS=test@fancypants.com";
	private static final long ROOT_VALIDITY = (long) (24 * 60 * 60);
	private static final String SERVER_X500_NAME = "CN=devices.test.fancypants.com, EMAILADDRESS=devices@test.fancypants.com";
	private static final long SERVER_VALIDITY = (long) (12 * 60 * 60);
	private static final long DEVICE_VALIDITY = (long) (6 * 60 * 60);
	private static final String ADMIN_X500_NAME = "CN=admin.test.fancypants.com, EMAILADDRESS=admin@test.fancypants.com";
	private static final long ADMIN_VALIDITY = (long) (6 * 60 * 60);
	private static final int KEY_LENGTH = 1024;

	@Bean(name = "serverKeyStore")
	@Autowired
	public KeyStore serverKeyStore(
			@Qualifier("rootCert") X509Certificate rootCert,
			@Qualifier("serverCert") X509Certificate serverCert,
			@Qualifier("serverKey") CertAndKeyGen serverKey,
			@Value("${keystore.file}") final Resource keystoreFile,
			@Value("${keystore.pass}") final String keystorePassword)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		// remove the old keystore
		keystoreFile.getFile().delete();
		KeyStore keystore = KeyStore.getInstance("JKS");
		// initialize a null keystore
		keystore.load(null, null);
		// insert the root cert
		keystore.setCertificateEntry("root", rootCert);
		// create the certification chain for the server cert
		X509Certificate serverChain[] = { serverCert, rootCert };
		// insert the server cert
		keystore.setKeyEntry("server", serverKey.getPrivateKey(),
				"server".toCharArray(), serverChain);
		// write out the keystore to file
		keystore.store(new FileOutputStream(keystoreFile.getFile()
				.getAbsolutePath()), keystorePassword.toCharArray());
		// done
		return keystore;
	}

	@Bean(name = "deviceKeyStore")
	@Autowired
	public KeyStore deviceKeyStore(
			@Qualifier("rootCert") X509Certificate rootCert,
			@Qualifier("deviceCert") X509Certificate deviceCert,
			@Qualifier("deviceKey") CertAndKeyGen deviceKey)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		KeyStore keystore = KeyStore.getInstance("JKS");
		// initialize a null keystore
		keystore.load(null, null);
		// insert the root cert
		keystore.setCertificateEntry("root", rootCert);
		// create the certification chain for the device cert
		X509Certificate deviceChain[] = { deviceCert, rootCert };
		// insert the server cert
		keystore.setKeyEntry("device", deviceKey.getPrivateKey(),
				"device".toCharArray(), deviceChain);
		return keystore;
	}

	@Bean(name = "adminKeyStore")
	@Autowired
	public KeyStore adminKeyStore(
			@Qualifier("rootCert") X509Certificate rootCert,
			@Qualifier("adminCert") X509Certificate adminCert,
			@Qualifier("adminKey") CertAndKeyGen adminKey)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		KeyStore keystore = KeyStore.getInstance("JKS");
		// initialize a null keystore
		keystore.load(null, null);
		// insert the root cert
		keystore.setCertificateEntry("root", rootCert);
		// create the certification chain for the admin cert
		X509Certificate adminChain[] = { adminCert, rootCert };
		// insert the server cert
		keystore.setKeyEntry("admin", adminKey.getPrivateKey(),
				"admin".toCharArray(), adminChain);
		return keystore;
	}

	@Bean(name = "rootKey")
	public CertAndKeyGen rootKey() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException {
		// generate the key
		CertAndKeyGen keyGen = new CertAndKeyGen("RSA", KEYGEN_ALGORITHM, null);
		keyGen.generate(KEY_LENGTH);
		return keyGen;
	}

	@Bean(name = "serverKey")
	public CertAndKeyGen serverKey() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException {
		// generate key
		CertAndKeyGen keyGen = new CertAndKeyGen("RSA", KEYGEN_ALGORITHM, null);
		keyGen.generate(KEY_LENGTH);
		return keyGen;
	}

	@Bean(name = "deviceKey")
	public CertAndKeyGen deviceKey() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException {
		// generate key
		CertAndKeyGen keyGen = new CertAndKeyGen("RSA", KEYGEN_ALGORITHM, null);
		keyGen.generate(KEY_LENGTH);
		return keyGen;
	}

	@Bean(name = "adminKey")
	public CertAndKeyGen adminKey() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException {
		// generate key
		CertAndKeyGen keyGen = new CertAndKeyGen("RSA", KEYGEN_ALGORITHM, null);
		keyGen.generate(KEY_LENGTH);
		return keyGen;
	}

	@Bean(name = "rootCert")
	@Autowired
	public X509Certificate rootCert(@Qualifier("rootKey") CertAndKeyGen rootKey)
			throws IOException, InvalidKeyException, CertificateException,
			SignatureException, NoSuchAlgorithmException,
			NoSuchProviderException {
		// create the root cert
		X500Name rootName = new X500Name(ROOT_X500_NAME);
		X509Certificate rootCert = rootKey.getSelfCertificate(rootName,
				ROOT_VALIDITY);
		// self-sign the root cert
		return certificateSign(rootKey, rootCert, rootCert);
	}

	@Bean(name = "serverCert")
	@Autowired
	public X509Certificate serverCert(
			@Qualifier("rootKey") CertAndKeyGen rootKey,
			@Qualifier("serverKey") CertAndKeyGen serverKey,
			@Qualifier("rootCert") X509Certificate rootCert)
			throws IOException, InvalidKeyException, CertificateException,
			SignatureException, NoSuchAlgorithmException,
			NoSuchProviderException {
		// create the server cert
		X500Name serverName = new X500Name(SERVER_X500_NAME);
		X509Certificate serverCert = serverKey.getSelfCertificate(serverName,
				SERVER_VALIDITY);
		// sign + return the certificate
		return certificateSign(rootKey, rootCert, serverCert);
	}

	@Bean(name = "deviceCert")
	@Autowired
	public X509Certificate deviceCert(
			@Qualifier("rootKey") CertAndKeyGen rootKey,
			@Qualifier("deviceKey") CertAndKeyGen deviceKey,
			@Qualifier("rootCert") X509Certificate rootCert)
			throws IOException, InvalidKeyException, CertificateException,
			SignatureException, NoSuchAlgorithmException,
			NoSuchProviderException {
		// create the device cert
		X500Name deviceName = new X500Name("CN="
				+ DeviceValues.DEVICEENTITY.getDevice() + ", EMAILADDRESS="
				+ DeviceValues.DEVICEENTITY.getDevice()
				+ "@devices.test.fancypants.com");
		X509Certificate deviceCert = deviceKey.getSelfCertificate(deviceName,
				DEVICE_VALIDITY);
		// sign + return the certificate
		return certificateSign(rootKey, rootCert, deviceCert);
	}

	@Bean(name = "adminCert")
	@Autowired
	public X509Certificate adminCert(
			@Qualifier("rootKey") CertAndKeyGen rootKey,
			@Qualifier("adminKey") CertAndKeyGen adminKey,
			@Qualifier("rootCert") X509Certificate rootCert)
			throws IOException, InvalidKeyException, CertificateException,
			SignatureException, NoSuchAlgorithmException,
			NoSuchProviderException {
		// create the admin cert
		X500Name adminName = new X500Name(ADMIN_X500_NAME);
		X509Certificate adminCert = adminKey.getSelfCertificate(adminName,
				ADMIN_VALIDITY);
		// sign + return the certificate
		return certificateSign(rootKey, rootCert, adminCert);
	}

	private X509Certificate certificateSign(CertAndKeyGen rootKey,
			X509Certificate rootCert, X509Certificate inCert)
			throws IOException, CertificateException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException {
		// get the root cert's issuer name
		X500Name nameRoot = new X500Name(rootCert.getIssuerX500Principal()
				.getName());
		// sign the server cert with the root cert
		byte[] bytes = inCert.getTBSCertificate();
		X509CertInfo info = new X509CertInfo(bytes);
		info.set(X509CertInfo.ISSUER, new CertificateIssuerName(nameRoot));
		X509CertImpl outCert = new X509CertImpl(info);
		outCert.sign(rootKey.getPrivateKey(), inCert.getSigAlgName());
		return outCert;
	}

}
