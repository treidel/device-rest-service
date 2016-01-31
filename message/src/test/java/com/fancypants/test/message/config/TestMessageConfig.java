package com.fancypants.test.message.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.test.message.TestMessageScanMe;

@Configuration
@ComponentScan(basePackageClasses = { TestMessageScanMe.class })
public class TestMessageConfig {

}
