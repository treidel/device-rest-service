package com.fancypants.storm.usage.filter;

import java.util.Map;
import java.util.logging.LogRecord;

import org.springframework.stereotype.Component;

import storm.trident.operation.Filter;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

@Component
public class PrintFilter implements Filter, java.util.logging.Filter {

	private static final long serialVersionUID = -1314812632170324414L;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TridentOperationContext context) {

	}

	@Override
	public void cleanup() {

	}

	@Override
	public boolean isLoggable(LogRecord arg0) {
		return false;
	}

	@Override
	public boolean isKeep(TridentTuple tuple) {
		System.out.println(tuple);
		return true;
	}

}
