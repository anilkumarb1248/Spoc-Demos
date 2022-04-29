package com.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.app.entity.EmployeeEntity;
import com.app.exceptions.DuplicateEmployeeException;
import com.app.exceptions.EmployeeNotFoundException;
import com.app.model.Employee;
import com.app.repository.EmployeeRepository;
import com.app.util.ResponseStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	@Autowired
	EmployeeRepository employeeRepository;
	
//	@Autowired
//	public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
//		this.employeeRepository = employeeRepository;
//	}

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
	public Employee getEmployee(int employeeId) {
		Optional<EmployeeEntity> optional = employeeRepository.findById(employeeId);
		
		if (!optional.isPresent()) {
			throw new EmployeeNotFoundException("No employee found with id:" + employeeId);
		}

		EmployeeEntity employeeEntity = optional.get();
		return convertToBean(employeeEntity);
	}

	@Override
	public ResponseStatus addEmployee(Employee employee) {

		if (!isDuplicateEmployee(true, employee)) {
			EmployeeEntity emp = employeeRepository.save(convertToEntity(employee));

			if (null != emp) {
				return createResponseStatus(HttpStatus.CREATED, "Employee added successfully");
			} else {
				return createResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY, "Failed to add Employee");
			}
		} else {
			throw new DuplicateEmployeeException("Employee already exist with name: " + employee.getEmpName());
		}

	}

	@Override
	public ResponseStatus addEmployees(List<Employee> employees) {

		List<EmployeeEntity> employeeEntities = new ArrayList<>();

		employees.stream().forEach(employee -> {
			if (!isDuplicateEmployee(true, employee)) {
				employeeEntities.add(convertToEntity(employee));
			}
		});

		if (!employeeEntities.isEmpty()) {
			employeeRepository.saveAll(employeeEntities);
		}

		return createResponseStatus(HttpStatus.CREATED, "Employees added successfully");
	}

	@Override
	public ResponseStatus updateEmployee(Employee employee) {

		if (isEmployeeExist(employee.getEmpId())) {
			if (isDuplicateEmployee(false, employee)) {
				throw new DuplicateEmployeeException("Employee already exist with name: " + employee.getEmpName());
			}

			employeeRepository.save(convertToEntity(employee));

			return createResponseStatus(HttpStatus.OK, "Employees updated successfully");

		} else {
			throw new EmployeeNotFoundException("No employee found with id: " + employee.getEmpId());
		}
	}

	@Override
	public ResponseStatus deleteEmployee(int employeeId) {

		if (isEmployeeExist(employeeId)) {
			employeeRepository.deleteById(employeeId);

			return createResponseStatus(HttpStatus.OK, "Employee deleted successfully");
		} else {
			throw new EmployeeNotFoundException("No employee found with id: " + employeeId);
		}
	}

	@Override
	public ResponseStatus deleteAll() {
		try {
//			employeeRepository.deleteAllInBatch(); // Not working due to Address constraints

			List<EmployeeEntity> entitiesList = employeeRepository.findAll();
			employeeRepository.deleteAll(entitiesList);
			return createResponseStatus(HttpStatus.OK, "Employees deleted successfully");
		} catch (Exception e) {
			return createResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete all employees");
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

	private ResponseStatus createResponseStatus(HttpStatus httpStatus, String message) {
		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setStatusCode(String.valueOf(httpStatus.value()));
		responseStatus.setMessage(message);
		return responseStatus;
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

	@Override
	@Cacheable(cacheNames = "employees", key = "#name")
	public Employee getEmployeeByName(String firstName) {

		Optional<EmployeeEntity> optional = employeeRepository.findByEmpName(firstName);
		if (!optional.isPresent()) {
			throw new EmployeeNotFoundException("No employee found with firstName:" + firstName);
		}

		EmployeeEntity employeeEntity = optional.get();
		return convertToBean(employeeEntity);
	}

	@Override
	public ResponseStatus addDummyData() {
		ResponseStatus status = null;
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
			status = createResponseStatus(HttpStatus.OK, "Inserted employees dummy data successfully");
		} catch (Exception e) {
			LOGGER.info("Failed to insert employee dummy data");
			LOGGER.error(e.getMessage());
			status = createResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to insert employee dummy data");
		}
		return status;
	}
}
