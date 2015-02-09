package com.fancypants.common.application.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

public class AnnotationUtils {

	public static List<Method> findAnnotatedMethods(Class<?> clazz,
			Class<? extends Annotation> annotationClass) {
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz); 
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotationClass)) {
				annotatedMethods.add(method);
			}
		}
		return annotatedMethods;
	}

}
