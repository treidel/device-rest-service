package com.fancypants.rest.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class CustomAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomAuthenticationFilter.class);

	public static final String DEVICE_PARAM = "device";
	public static final String SERIALNUMBER_PARAM = "serialnumber";

	protected CustomAuthenticationFilter() {
		// use our custom request matcher
		super(new CustomRequestMatcher());
		// override the default success handler
		setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler());
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

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// do the normal stuff
		super.successfulAuthentication(request, response, chain, authResult);
		// run the rest of the filter chain
		chain.doFilter(request, response);
	}
}
