package com.app.util;

public class ErrorResponse {

	private String statusCode;
	private String errorMessage;

	public ErrorResponse() {
	}

	public ErrorResponse(String statusCode, String errorMessage) {
		super();
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
