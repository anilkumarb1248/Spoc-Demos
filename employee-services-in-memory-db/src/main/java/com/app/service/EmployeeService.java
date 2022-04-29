package com.app.service;

import java.util.List;

import com.app.model.Employee;
import com.app.util.ResponseStatus;

public interface EmployeeService {

	public List<Employee> getEmployeeList();

	public Employee getEmployee(int employeeId);

	public ResponseStatus addEmployee(Employee employee);

	public ResponseStatus addEmployees(List<Employee> employees);

	public ResponseStatus updateEmployee(Employee employee);

	public ResponseStatus deleteEmployee(int employeeId);

	public ResponseStatus deleteAll();

	public Employee getEmployeeByName(String name);

	public ResponseStatus addDummyData();
	
}
