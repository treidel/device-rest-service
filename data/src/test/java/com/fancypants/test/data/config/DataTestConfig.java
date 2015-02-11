package com.fancypants.test.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.CommonScanMe;
import com.fancypants.test.data.TestDataScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, TestDataScanMe.class })
public class DataTestConfig {

}
