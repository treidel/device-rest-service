package com.fancypants.common.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class Application {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);
	private static final String PACKAGES_RESOURCES = "packages.list";
	private static final String PACKAGES_TEST_RESOURCES = "packages.list.test";
	private static final String IGNORE_TEST_PACKAGES_ENVVAR = "IGNORE_TEST_PACKAGES";

	public static void main(String[] args) throws Exception {
		LOG.trace("main enter", "args", args);
		// calculate which packages to search for components
		Package[] packages = computeSearchPath();
		// create the args for SpringApplication
		ArrayList<Object> objects = new ArrayList<Object>(1 + packages.length);
		objects.add(Application.class);
		objects.addAll(Arrays.asList(packages));
		// start the spring application using this search path
		SpringApplication application = new SpringApplication(objects.toArray());
		application.run(args);
		LOG.trace("main exit", "args", args);
	}

	private static Package[] computeSearchPath() {
		LOG.trace("computeSearchPath enter");

		// assume we won't find any packages
		Package[] packages = new Package[0];

		// see if we have a package list
		Resource resource = new ClassPathResource(PACKAGES_RESOURCES);
		if (true == resource.exists()) {
			// see if there's a test override
			Resource testResource = new ClassPathResource(PACKAGES_TEST_RESOURCES);
			// only use the override if the ignore envvar isn't set
			if ((true == testResource.exists()) && (null == System.getenv(IGNORE_TEST_PACKAGES_ENVVAR))) {
				LOG.debug("using test packages");
				resource = testResource;
			}
			// now load the package list
			try {
				// parse the package list
				BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
				String line = reader.readLine();
				// setup the list of packages we read
				List<Package> packageList = new LinkedList<Package>();
				while (null != line) {
					// find the package object
					Package pkg = Class.forName(line).getPackage();
					LOG.info("Adding package={} to component search path", pkg.toString());
					// add it to the list
					packageList.add(pkg);
					// read next line
					line = reader.readLine();
				}
				// convert to an array
				packages = new Package[packageList.size()];
				packageList.toArray(packages);
				return packages;
			} catch (IOException e) {
				LOG.error("IOException reading package list", e);
				throw new IllegalStateException(e);
			} catch (ClassNotFoundException e) {
				LOG.error("ClassNotFoundException reading package list", e);
				throw new IllegalStateException(e);
			}
		}
		LOG.trace("exit {}", (Object) packages);
		return packages;
	}
}
