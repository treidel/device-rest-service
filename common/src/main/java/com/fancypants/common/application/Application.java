package com.fancypants.common.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@EnableAutoConfiguration
public class Application {

	private static final Logger LOG = Logger.getLogger(Application.class);
	private static final String PACKAGES_RESOURCES = "packages.list";

	public static void main(String[] args) throws Exception {
		LOG.trace("main enter" + " args" + args);
		// calculate which packages to search for components
		Package[] packages = computeSearchPath();
		// create the args for SpringApplication
		ArrayList<Object> objects = new ArrayList<Object>(1 + packages.length);
		objects.add(Application.class);
		objects.addAll(Arrays.asList(packages));
		// start the spring application using this search path
		SpringApplication application = new SpringApplication(
				objects.toArray());
		application.run(args);
		LOG.trace("main exit" + " args" + args);
	}

	private static Package[] computeSearchPath() {
		// load the package list
		Resource resource = new ClassPathResource(PACKAGES_RESOURCES);
		try {
			// parse the package list
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resource.getInputStream()));
			String line = reader.readLine();
			// setup the list of packages we read
			List<Package> packageList = new LinkedList<Package>();
			while (null != line) {
				// find the package object
				Package pkg = Class.forName(line).getPackage();
				// add it to the list
				packageList.add(pkg);
				// read next line
				line = reader.readLine();
			}
			// convert to an array
			Package[] packages = new Package[packageList.size()];
			packageList.toArray(packages);
			return packages;
		} catch (IOException e) {
			LOG.fatal("IOException reading package list", e);
			return null;
		} catch (ClassNotFoundException e) {
			LOG.fatal("ClassNotFoundException reading package list", e);
			return null;
		}
	}
}
