package com.fancypants.jobs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.data.DataScanMe;

@Configuration
@ComponentScan(basePackageClasses = { DataScanMe.class })
public class JobsConfig {

}
