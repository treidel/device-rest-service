package com.fancypants.rest.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.rest.domain.ErrorMessage;

@ControllerAdvice()
public class RESTControllerAdvice {

	@ExceptionHandler(BusinessLogicException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	ErrorMessage handleBusinessLogicException(BusinessLogicException ex) {
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
		return errorMessage;
	}

	@ExceptionHandler(DataValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorMessage handleDataValidationException(DataValidationException ex) {
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
		return errorMessage;
	}
	
}
