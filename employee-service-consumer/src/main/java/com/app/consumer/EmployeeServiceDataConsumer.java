package com.app.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.client.EmployeeClient;
import com.app.model.Employee;
import com.app.util.ResponseStatus;

@RestController
@RequestMapping("/employee")
public class EmployeeServiceDataConsumer {
	
	private EmployeeClient employeeClient;
	
	@Autowired
	public EmployeeServiceDataConsumer(EmployeeClient employeeClient) {
		this.employeeClient = employeeClient;
	}
	
	@GetMapping("/list")
	public List<Employee> getEmployeeList() {
		return employeeClient.getEmployeeList();
	}

	@GetMapping("/getById/{id}")
	public Employee getEmployee(@PathVariable(value = "id") int employeeId) {
		return employeeClient.getEmployee(employeeId);
	}
	
	@GetMapping("/getByName")
	public Employee getEmployeeByName(@RequestParam String name) {
		return employeeClient.getEmployeeByName(name);
	}

	@PostMapping("/add")
	public ResponseStatus addEmployee(@RequestBody Employee employee) {
		return employeeClient.addEmployee(employee);
	}

	@PostMapping("/addList")
	public ResponseStatus addEmployees(@RequestBody List<Employee> employees) {
		return employeeClient.addEmployees(employees);
	}

	@PutMapping("/update")
	public ResponseStatus updateEmployee(@RequestBody Employee employee) {
		return employeeClient.updateEmployee(employee);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseStatus deleteEmployee(@PathVariable(value = "id") int employeeId) {
		return employeeClient.deleteEmployee(employeeId);
	}

	@DeleteMapping("/deleteAll")
	public ResponseStatus deleteAll() {
		return employeeClient.deleteAll();
	}

}
