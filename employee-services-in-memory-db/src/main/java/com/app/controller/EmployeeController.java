package com.app.controller;

import java.util.List;

import com.app.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.model.Employee;

@RequestMapping("/employee")
public interface EmployeeController {

	@GetMapping({ "/list", "/all", "/employees" })
	public ResponseEntity<ApiResponse<List<Employee>>> getEmployeeList();

	@GetMapping("/getById/{id}")
	public ResponseEntity<ApiResponse> getEmployee(@PathVariable(value = "id") String employeeId);
	
	@GetMapping("/getByName")
	public ResponseEntity<ApiResponse> getEmployeeByName(@RequestParam String name);

	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addEmployee(@RequestBody Employee employee);

	@PostMapping("/addList")
	public ResponseEntity<ApiResponse> addEmployees(@RequestBody List<Employee> employees);

	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateEmployee(@RequestBody Employee employee);

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable(value = "id") String employeeId);

	@DeleteMapping("/deleteAll")
	public ResponseEntity<ApiResponse> deleteAll();

	@GetMapping("/dummyData")
	public ResponseEntity<ApiResponse> addDummyData();
}
