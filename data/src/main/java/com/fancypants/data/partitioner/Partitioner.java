package com.fancypants.data.partitioner;

public interface Partitioner<T> {
	String partition(T entity);
}