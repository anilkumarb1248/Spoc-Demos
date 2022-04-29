package com.app.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	private String statusCode;
	private String message;
	private String errorMessage;

//	public ResponseStatus() {
//	}
//
//	public ResponseStatus(String statusCode, String message, String errorMessage) {
//		this.statusCode = statusCode;
//		this.message = message;
//		this.errorMessage = errorMessage;
//	}

}
