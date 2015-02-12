package com.fancypants.test.stream.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fancypants.stream.writer.StreamWriter;

public class TestStreamWriter<T> implements StreamWriter<T> {
	private static final Logger LOG = LoggerFactory.getLogger(TestStreamWriter.class);
	
	
	@Override
	public void putRecord(String hashKey, T record) {
		LOG.info("record receive with hashKey=" + hashKey);
	}

}
