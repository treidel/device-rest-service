package com.fancypants.stream.writer;

import com.fancypants.stream.exception.StreamException;


public interface StreamWriter<T> {

	void putRecord(String hashKey, T record) throws StreamException;

}
