package com.fancypants.data.partitioner;

public interface Partitioner<E, T> {
	Partition partitionByEntity(E entity);

	Partition partitionByValue(T value);
}