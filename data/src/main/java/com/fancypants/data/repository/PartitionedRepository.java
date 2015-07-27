package com.fancypants.data.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

public interface PartitionedRepository<E, I extends Serializable> {

	void createPartition(String partition);

	void deletePartition(String partition);

	CrudRepository<E, I> retrievePartitionTable(String partition);

}
