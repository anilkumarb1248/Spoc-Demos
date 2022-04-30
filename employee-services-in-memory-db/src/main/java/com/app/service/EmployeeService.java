package com.app.service;

import java.util.List;

import com.app.model.ApiResponse;
import com.app.model.Employee;

public interface EmployeeService {

	public List<Employee> getEmployeeList();

	public ApiResponse<Employee> getEmployee(String employeeId);

	public ApiResponse<Employee> getEmployeeByName(String firstName);

	public ApiResponse addEmployee(Employee employee);

	public ApiResponse addEmployees(List<Employee> employees);

	public ApiResponse updateEmployee(Employee employee);

	public ApiResponse deleteEmployee(String employeeId);

	public ApiResponse deleteAll();

	public ApiResponse addDummyData();
	
}
