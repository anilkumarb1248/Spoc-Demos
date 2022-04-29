package com.app.exceptions;

import org.springframework.web.client.RestClientException;

public class ClientException extends RuntimeException{

	private static final long serialVersionUID = -5397108605142360059L;
	
	private final Class<?> clientClass;
	
	public ClientException(RestClientException cause, Class<?> clientClass) {
		super(cause);
		this.clientClass = clientClass;
	}
	
	public Class<?> getClientClass(){
		return clientClass;
	}
	
	@Override
	public String getMessage() {
		return "Exception occurred in client class: " + clientClass + " with error message: " + super.getMessage();
	}

}
