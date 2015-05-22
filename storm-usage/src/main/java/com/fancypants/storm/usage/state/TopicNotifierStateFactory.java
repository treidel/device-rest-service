package com.fancypants.storm.usage.state;

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
import com.fancypants.message.topic.TopicManager;

@Component
public class TopicNotifierStateFactory implements StateFactory {

	private static final long serialVersionUID = -8884066339861949756L;

	@Autowired
	private TopicManager topicManager;

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		// re-initialize the manager
		List<Method> methods = AnnotationUtils.findAnnotatedMethods(
				topicManager.getClass(), PostConstruct.class);
		for (Method method : methods) {
			ReflectionUtils.makeAccessible(method);
			ReflectionUtils.invokeMethod(method, topicManager);
		}
		// create the backing map
		TopicNotifierState state = new TopicNotifierState(topicManager);
		return state;
	}

}
