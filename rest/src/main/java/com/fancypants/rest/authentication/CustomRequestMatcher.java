package com.fancypants.rest.authentication;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CustomRequestMatcher implements RequestMatcher {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomRequestMatcher.class);

	@Override
	public boolean matches(HttpServletRequest request) {
		LOG.trace("CustomRequestMatcher.matches enter {}={}", "request",
				request);
		boolean result = (null != request
				.getParameter(CustomAuthenticationFilter.DEVICE_PARAM))
				&& (null != request
						.getParameter(CustomAuthenticationFilter.SERIALNUMBER_PARAM));
		LOG.trace("CustomRequestMatcher.matches exit {}", result);
		return result;
	}
}
