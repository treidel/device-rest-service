package com.fancypants.storm.processing.device.record.state;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

import com.fancypants.common.application.util.AnnotationUtils;
import com.fancypants.data.repository.HourlyRecordRepository;

@Component
public class UsageStateFactory implements StateFactory {

	private static final long serialVersionUID = 296987272885779417L;

	@Autowired
	private HourlyRecordRepository repository;

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		// re-initialize the repository
		List<Method> methods = AnnotationUtils.findAnnotatedMethods(
				repository.getClass(), PostConstruct.class);
		for (Method method : methods) {
			ReflectionUtils.makeAccessible(method);
			ReflectionUtils.invokeMethod(method, repository);
		}
		// create the backing map
		UsageState state = new UsageState(repository);

		return state;
	}

}
