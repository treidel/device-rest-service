package com.fancypants.common.mapping;

public interface EntityMapper<T, F> {
	T convert(F entity);
}
