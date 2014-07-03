package com.fancypants.rest.device.mapping;

public interface EntityMapper<T, F> {
	T convert(F entity);
}
