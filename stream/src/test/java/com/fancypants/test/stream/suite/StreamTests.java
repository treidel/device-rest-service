package com.fancypants.test.stream.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.stream.writer.StreamWriter;
import com.fancypants.test.data.values.RawRecordValues;
import com.fancypants.test.stream.config.StreamTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = StreamTestConfig.class)
public class StreamTests {

	@Autowired
	private StreamWriter<RawRecordEntity> rawRecordStreamWriter;

	@Test
	public void writeTest() throws Exception {
		// pick the record
		RawRecordEntity record = RawRecordValues.RECORD1;
		// write the record
		rawRecordStreamWriter.putRecord(record.getDevice(), record);
	}
}
