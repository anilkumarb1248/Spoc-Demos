package com.app.global.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.app.exceptions.DuplicateEmployeeException;
import com.app.exceptions.EmployeeNotFoundException;

@RestControllerAdvice
public class EmployeeExceptionHandler {

//	@ExceptionHandler(value = { DuplicateEmployeeException.class })
//	@ResponseStatus(HttpStatus.CONFLICT)
//	public ResponseEntity<ErrorResponse> handleDuplicateEmployeeException(DuplicateEmployeeException ex) {
//		ErrorResponse errorResponse = new ErrorResponse();
//		errorResponse.setStatusCode(String.valueOf(HttpStatus.CONFLICT.value()));
//		errorResponse.setErrorMessage(ex.getMessage());
//		return ResponseEntity.ok(errorResponse);
//	}
//
//	@ExceptionHandler(value = { EmployeeNotFoundException.class })
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
//		ErrorResponse errorResponse = new ErrorResponse();
//		errorResponse.setStatusCode(String.valueOf(HttpStatus.NO_CONTENT.value()));
//		errorResponse.setErrorMessage(ex.getMessage());
//		return ResponseEntity.ok(errorResponse);
//	}

}
