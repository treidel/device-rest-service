package com.fancypants.usage.summarizer;

public interface Summarizer<I, T> {

	public T summarize(I id, T entity1, T entity2);
}
