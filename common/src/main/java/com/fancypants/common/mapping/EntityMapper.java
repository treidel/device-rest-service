package com.fancypants.common.mapping;

import com.fancypants.common.exception.DataValidationException;

public interface EntityMapper<T, F> {
	T convert(F entity) throws DataValidationException;
}
