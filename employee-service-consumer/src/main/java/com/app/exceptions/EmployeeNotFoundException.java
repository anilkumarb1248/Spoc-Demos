package com.app.exceptions;

public class EmployeeNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -6186025423126419617L;

	public EmployeeNotFoundException(String cause) {
		super(cause);
	}

}
