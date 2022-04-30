package com.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.app.entity.EmployeeEntity;
import com.app.exceptions.DuplicateEmployeeException;
import com.app.exceptions.EmployeeNotFoundException;
import com.app.model.ApiResponse;
import com.app.model.Error;
import com.app.model.Status;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.app.model.Employee;
import com.app.repository.EmployeeRepository;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	private EmployeeRepository employeeRepository;
	
	@Autowired
	public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}


	@Override
	public List<Employee> getEmployeeList() {
		List<EmployeeEntity> entitiesList = employeeRepository.findAll();
		List<Employee> employeesList = new ArrayList<>();
		entitiesList.stream().forEach(employeeEntity -> {
			employeesList.add(convertToBean(employeeEntity));
		});
		return employeesList;
	}

	@Override
	@Cacheable(cacheNames = "employees", key = "#employeeId")
	public ApiResponse<Employee> getEmployee(String employeeId) {
		Optional<EmployeeEntity> optional = employeeRepository.findById(Integer.valueOf(employeeId));

		if (!optional.isPresent()) {
			throw new EmployeeNotFoundException("No employee found with id: " + employeeId);
		}
		ApiResponse<Employee> apiResponse = new ApiResponse<>();
		apiResponse.setStatus(new Status(String.valueOf(HttpStatus.OK.value()), "Found employee details by Id: " + employeeId));
		apiResponse.setPayload(convertToBean(optional.get()));
		return apiResponse;
	}

	@Override
	@Cacheable(cacheNames = "employees", key = "#firstName")
	public ApiResponse<Employee> getEmployeeByName(String firstName) {
		Optional<EmployeeEntity> optional = employeeRepository.findByEmpName(firstName);
		if (!optional.isPresent()) {
			throw new EmployeeNotFoundException("No employee found with firstName: " + firstName);
		}

		ApiResponse<Employee> apiResponse = new ApiResponse<>();
		apiResponse.setStatus(new Status(String.valueOf(HttpStatus.OK.value()), "Found employee details by name: " + firstName));
		apiResponse.setPayload(convertToBean(optional.get()));
		return apiResponse;
	}

	@Override
	public ApiResponse addEmployee(Employee employee) {
		if (!isDuplicateEmployee(true, employee)) {
			EmployeeEntity emp = employeeRepository.save(convertToEntity(employee));
			if (null != emp) {
				return createApiResponse("SUCCESS",HttpStatus.CREATED, "Employee added successfully", null);
			} else {
				return createApiResponse("FAILURE", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add Employee", HttpStatus.INTERNAL_SERVER_ERROR.toString());
			}
		} else {
			throw new DuplicateEmployeeException("Employee already exist with name: " + employee.getEmpName());
		}
	}

	@Override
	public ApiResponse addEmployees(List<Employee> employees) {
		List<EmployeeEntity> employeeEntities = new ArrayList<>();

		employees.stream().forEach(employee -> {
			if (!isDuplicateEmployee(true, employee)) {
				employeeEntities.add(convertToEntity(employee));
			}
		});

		if (!employeeEntities.isEmpty()) {
			employeeRepository.saveAll(employeeEntities);
		}

		return createApiResponse("SUCCESS",HttpStatus.CREATED, "Employees added successfully", null);
	}

	@Override
	public ApiResponse updateEmployee(Employee employee) {
		if (isEmployeeExist(employee.getEmpId())) {
			if (isDuplicateEmployee(false, employee)) {
				throw new DuplicateEmployeeException("Employee already exist with name: " + employee.getEmpName());
			}
			employeeRepository.save(convertToEntity(employee));

			return createApiResponse("SUCCESS",HttpStatus.OK, "Employee updated successfully", null);
		} else {
			throw new EmployeeNotFoundException("No employee found with id: " + employee.getEmpId());
		}
	}

	@Override
	public ApiResponse deleteEmployee(String employeeId) {
		if (isEmployeeExist(Integer.valueOf(employeeId))) {
			employeeRepository.deleteById(Integer.valueOf(employeeId));
			return createApiResponse("SUCCESS",HttpStatus.OK, "Employee deleted successfully", null);
		} else {
			throw new EmployeeNotFoundException("No employee found with id: " + employeeId);
		}
	}

	@Override
	public ApiResponse deleteAll() {
		try {
//			employeeRepository.deleteAllInBatch(); // Not working due to Address constraints

			List<EmployeeEntity> entitiesList = employeeRepository.findAll();
			employeeRepository.deleteAll(entitiesList);
			return createApiResponse("SUCCESS",HttpStatus.OK, "Employees deleted successfully", null);
		} catch (Exception e) {
			return createApiResponse("FAILURE",HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete all employees", e.getMessage());
		}
	}

	@Override
	public ApiResponse addDummyData() {
		LOGGER.info("Inserting employees dummy data");
		try {
			ObjectMapper mapper = new ObjectMapper();
			Resource resource = new ClassPathResource("employees-data.json");
			InputStream inputStream = resource.getInputStream();
			TypeReference<List<Employee>> typeReference = new TypeReference<List<Employee>>() {
			};

			List<Employee> list = mapper.readValue(inputStream, typeReference);

			List<EmployeeEntity> entityList = new ArrayList<>();
			list.forEach(employee -> {
				if (!isDuplicateEmployee(true, employee)) {
					entityList.add(convertToEntity(employee));
				}
			});
			employeeRepository.saveAll(entityList);
			LOGGER.info("Inserted employees dummy data successfully");
			return createApiResponse("SUCCESS",HttpStatus.OK, "Inserted employees dummy data successfully", null);
		} catch (Exception e) {
			LOGGER.info("Failed to insert employee dummy data");
			LOGGER.error(e.getMessage());
			return createApiResponse("FAILURE",HttpStatus.INTERNAL_SERVER_ERROR, "Failed to insert employee dummy data", e.getMessage());
		}
	}

	private Employee convertToBean(EmployeeEntity employeeEntity) {
		Employee employee = new Employee();
		BeanUtils.copyProperties(employeeEntity, employee);
		return employee;
	}

	private EmployeeEntity convertToEntity(Employee employee) {
		EmployeeEntity employeeEntity = new EmployeeEntity();
		BeanUtils.copyProperties(employee, employeeEntity);
		return employeeEntity;
	}

	private ApiResponse createApiResponse(String successFlag, HttpStatus httpStatus, String message, String description) {
		ApiResponse apiResponse = new ApiResponse();
		if("SUCCESS".equalsIgnoreCase(successFlag)){
			apiResponse.setStatus(new Status(String.valueOf(httpStatus.value()),message));
		}else{
			apiResponse.setError(new Error(String.valueOf(httpStatus.value()), message, description));
		}
		return apiResponse;
	}

	private boolean isDuplicateEmployee(boolean newFlag, Employee employee) {
		Optional<EmployeeEntity> optional = employeeRepository.findByEmpName(employee.getEmpName());
		if (!optional.isPresent()) {
			return false;
		} else {
			EmployeeEntity duplicateEntity = optional.get();
			if (newFlag || duplicateEntity.getEmpId() != employee.getEmpId()) {
				return true;
			}
		}
		return false;
	}

	private boolean isEmployeeExist(int id) {
//		Optional<EmployeeEntity> optional = employeeRepository.findById(id);
//		return optional.isPresent();
		return employeeRepository.existsById(id);
	}
}
