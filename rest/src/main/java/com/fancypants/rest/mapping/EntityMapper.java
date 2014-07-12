package com.fancypants.rest.mapping;

public interface EntityMapper<T, F> {
	T convert(F entity);
}
