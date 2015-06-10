package com.fancypants.rest.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CustomAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomAuthenticationFilter.class);

	private static final String DEVICE_PARAM = "device";
	private static final String SERIALNUMBER_PARAM = "serialnumber";

	protected CustomAuthenticationFilter() {
		super(new CustomRequestMatcher());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		LOG.trace("attemptAuthentication enter {}={} {}={}", "request",
				request, "response", response);
		// fetch the parameters, must exist since we've filtered for them
		String device = request.getParameter(DEVICE_PARAM);
		assert null != device;
		String serialNumber = request.getParameter(SERIALNUMBER_PARAM);
		assert null != serialNumber;

		// create the token
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				device, serialNumber);

		// do the authentication
		Authentication authentication = getAuthenticationManager()
				.authenticate(authRequest);

		LOG.trace("attemptAuthentication exit {}", authentication);
		return authentication;
	}

	private static final class CustomRequestMatcher implements RequestMatcher {

		@Override
		public boolean matches(HttpServletRequest request) {
			LOG.trace("CustomRequestMatcher.matches enter {}={}", "request",
					request);
			boolean result = (null != request.getParameter(DEVICE_PARAM))
					&& (null != request.getParameter(SERIALNUMBER_PARAM));
			LOG.trace("CustomRequestMatcher.matches exit {}", result);
			return result;
		}

	}
}
