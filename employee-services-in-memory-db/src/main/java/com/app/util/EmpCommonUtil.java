package com.app.util;

import com.app.model.ApiResponse;
import com.app.model.Error;
import com.app.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EmpCommonUtil {

    public static ResponseEntity<ApiResponse> createBadRequestResponseEntity(String message){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(new Status(String.valueOf(HttpStatus.BAD_REQUEST.value()), message));
        apiResponse.setError(new Error(String.valueOf(HttpStatus.BAD_REQUEST.value()), message, HttpStatus.BAD_REQUEST.name()));
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ApiResponse> createFailureResponse(HttpStatus httpStatus, String errorMessage, String description){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setError(new Error(String.valueOf(httpStatus.value()), errorMessage, description));
        return new ResponseEntity<>(apiResponse, httpStatus);
    }
}
