package com.fancypants.rest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataPersistenceException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.rest.domain.ErrorMessage;

@ControllerAdvice()
public class RESTControllerAdvice {

	private static final Logger LOG = LoggerFactory
			.getLogger(RESTControllerAdvice.class);

	@ExceptionHandler(BusinessLogicException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	ErrorMessage handleBusinessLogicException(BusinessLogicException ex) {
		LOG.trace("handleBusinessLogicException enter {}={}", "ex", ex);
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
		LOG.trace("handleBusinessLogicException exit {}", errorMessage);
		return errorMessage;
	}

	@ExceptionHandler(DataValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorMessage handleDataValidationException(DataValidationException ex) {
		LOG.trace("handleDataValidationException enter {}={}", "ex", ex);
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
		LOG.trace("handleDataValidationException exit {}", errorMessage);
		return errorMessage;
	}

	@ExceptionHandler(DataPersistenceException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ResponseBody
	ErrorMessage handleDataPersistenceException(DataPersistenceException ex) {
		LOG.trace("handleDataPersistenceException enter {}={}", "ex", ex);
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
		LOG.trace("handleDataPersistenceException exit {}", errorMessage);
		return errorMessage;
	}

}
