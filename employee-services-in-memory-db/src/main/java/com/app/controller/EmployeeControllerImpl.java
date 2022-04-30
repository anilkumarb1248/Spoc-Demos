package com.app.controller;

import java.util.List;

import com.app.exceptions.DuplicateEmployeeException;
import com.app.exceptions.EmployeeNotFoundException;
import com.app.model.ApiResponse;
import com.app.model.Error;
import com.app.util.EmpCommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.Employee;
import com.app.service.EmployeeService;

@RestController
public class EmployeeControllerImpl implements EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<ApiResponse<List<Employee>>> getEmployeeList() {
        List<Employee> employeeList = employeeService.getEmployeeList();
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        if(null == employeeList || employeeList.isEmpty()){
            apiResponse.setError(new Error(String.valueOf(HttpStatus.NOT_FOUND.value()), "Employee table is empty", HttpStatus.NOT_FOUND.name()));
        }else{
            apiResponse.setPayload(employeeList);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> getEmployee(String employeeId) {
        if (StringUtils.isBlank(employeeId) || !StringUtils.isNumeric(employeeId)) {
            return EmpCommonUtil.createBadRequestResponseEntity("Id should not be blank or non numeric");
        }
        try {
            return new ResponseEntity<>(employeeService.getEmployee(employeeId), HttpStatus.OK);
        } catch (EmployeeNotFoundException e) {
            return EmpCommonUtil.createFailureResponse(HttpStatus.NOT_FOUND, e.getMessage(), "Get employee by id failed");
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getEmployeeByName(String name) {

        if (StringUtils.isBlank(name)) {
            return EmpCommonUtil.createBadRequestResponseEntity("Name should not be blank");
        }
        try {
            return new ResponseEntity<>(employeeService.getEmployeeByName(name), HttpStatus.OK);
        } catch (EmployeeNotFoundException e) {
            return EmpCommonUtil.createFailureResponse(HttpStatus.NOT_FOUND, e.getMessage(), "Get employee by name failed");
        }
    }


    @Override
    public ResponseEntity<ApiResponse> addEmployee(Employee employee) {
        try{
            return new ResponseEntity<>(employeeService.addEmployee(employee), HttpStatus.CREATED);
        }catch(DuplicateEmployeeException e){
            return EmpCommonUtil.createFailureResponse(HttpStatus.CONFLICT, e.getMessage(), HttpStatus.CONFLICT.name());
        }
    }

    @Override
    public ResponseEntity<ApiResponse> addEmployees(List<Employee> employees) {
        return new ResponseEntity<>(employeeService.addEmployees(employees), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse> updateEmployee(Employee employee) {
        try{
            return new ResponseEntity<>(employeeService.updateEmployee(employee), HttpStatus.OK);
        }catch(EmployeeNotFoundException e){
            return EmpCommonUtil.createFailureResponse(HttpStatus.FAILED_DEPENDENCY, e.getMessage(), HttpStatus.FAILED_DEPENDENCY.name());
        }
    }

    @Override
    public ResponseEntity<ApiResponse> deleteEmployee(String employeeId) {
        if (StringUtils.isBlank(employeeId) || !StringUtils.isNumeric(employeeId)) {
            return EmpCommonUtil.createBadRequestResponseEntity("Id should not be blank or non numeric");
        }
        try {
            return new ResponseEntity<>(employeeService.deleteEmployee(employeeId), HttpStatus.OK);
        } catch (EmployeeNotFoundException e) {
            return EmpCommonUtil.createFailureResponse(HttpStatus.NOT_FOUND, e.getMessage(), "Delete employee by id failed");
        }
    }

    @Override
    public ResponseEntity<ApiResponse> deleteAll() {
        return new ResponseEntity<>(employeeService.deleteAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> addDummyData() {
        return new ResponseEntity<>(employeeService.addDummyData(), HttpStatus.OK);
    }
}
