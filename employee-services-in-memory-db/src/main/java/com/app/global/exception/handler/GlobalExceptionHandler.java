package com.app.global.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.app.exceptions.DuplicateEmployeeException;
import com.app.exceptions.EmployeeNotFoundException;
import com.app.util.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Added in separate class not picking those handlers
	@ExceptionHandler(value = { DuplicateEmployeeException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ErrorResponse> handleDuplicateEmployeeException(DuplicateEmployeeException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatusCode(String.valueOf(HttpStatus.CONFLICT.value()));
		errorResponse.setErrorMessage(ex.getMessage());
		return ResponseEntity.ok(errorResponse);
	}

	@ExceptionHandler(value = { EmployeeNotFoundException.class })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatusCode(String.valueOf(HttpStatus.NO_CONTENT.value()));
		errorResponse.setErrorMessage(ex.getMessage());
		return ResponseEntity.ok(errorResponse);
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
		errorResponse.setErrorMessage(ex.getMessage());
		return ResponseEntity.ok(errorResponse);
	}

	// Check - Can we handler INTERNAL_SERVER_ERROR
	@ExceptionHandler(value = InternalServerError.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleServerError(InternalServerError ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		errorResponse.setErrorMessage("Internal Server Error Occurred");
		return ResponseEntity.ok(errorResponse);
	}

}
