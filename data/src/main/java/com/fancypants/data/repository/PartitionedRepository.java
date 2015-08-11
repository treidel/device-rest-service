package com.fancypants.data.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.fancypants.data.partitioner.Partition;

public interface PartitionedRepository<E, I extends Serializable> {

	void createPartition(Partition partition);

	void deletePartition(Partition partition);

	CrudRepository<E, I> retrievePartitionTable(Partition partition);

	List<Partition> listPartitions();

}
