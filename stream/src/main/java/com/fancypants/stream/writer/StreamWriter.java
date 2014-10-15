package com.fancypants.stream.writer;


public interface StreamWriter<T> {

	void putRecord(String hashKey, T record);

}
