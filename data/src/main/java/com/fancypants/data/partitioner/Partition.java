package com.fancypants.data.partitioner;

public class Partition {

	private final String value;

	Partition(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object object) {
		if (false == (object instanceof Partition)) {
			return false;
		}
		if (object == this) {
			return true;
		}
		Partition partition = (Partition) object;
		return value.equals(partition.value);
	}

	@Override
	public String toString() {
		return "Partition[value=" + value + "]";
	}
}
