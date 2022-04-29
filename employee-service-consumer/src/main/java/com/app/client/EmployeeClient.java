package com.app.client;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.app.exceptions.ClientException;
import com.app.exceptions.EmployeeNotFoundException;
import com.app.model.Employee;

import org.springframework.web.util.UriComponentsBuilder;
import com.app.util.ResponseStatus;

@Service
public class EmployeeClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeClient.class);

	private RestTemplate restTemplate;

	@Value("${employee.service.url}")
	private String employeeServiceURL;

	private static final String GET_EMPLOYEES_LIST_URL = "/list";
	private static final String GET_EMPLOYEE_BY_ID_URL = "/getById/{id}";
	private static final String GET_EMPLOYEE_BY_NAME_URL = "/getByName?name={name}";

	private static final String ADD_EMPLOYEE_URL = "/add";
	private static final String ADD_EMPLOYEES_LIST_URL = "/addList";
	private static final String UPDATE_EMPLOYEE_URL = "/update";
	private static final String DELETE_EMPLOYEE_URL = "/delete/{id}";
	private static final String DELETE_ALL_EMPLOYEES_URL = "/deleteAll";

	@Autowired
	public EmployeeClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private HttpHeaders buildHeaders() {
		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		return headers;
	}

	public List<Employee> getEmployeeList() {
		List<Employee> employeesList = null;
		HttpEntity<List<Employee>> entity = new HttpEntity<>(buildHeaders());

		try {
			ResponseEntity<List<Employee>> responseEntity = restTemplate.exchange(
					employeeServiceURL + GET_EMPLOYEES_LIST_URL, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<Employee>>() {
					});

			LOGGER.info("Successful call to get employee list: {}", responseEntity.getStatusCode());
			if (responseEntity.getStatusCode() == HttpStatus.OK && null != responseEntity.getBody()) {
				employeesList = responseEntity.getBody();
			}
		} catch (RestClientException e) {
			LOGGER.error("Caught RestClientException while fetching the employees list, error: {}", e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}

		return employeesList;
	}

	public Employee getEmployee(int employeeId) {

		URI uri = UriComponentsBuilder.fromUriString(employeeServiceURL + GET_EMPLOYEE_BY_ID_URL)
				.buildAndExpand(String.valueOf(employeeId)).toUri();

		LOGGER.info("Calling the get employee by id endpoint with uri: {}", uri.getRawPath());

		try {
			ResponseEntity<Employee> responseEntity = restTemplate.exchange(uri, HttpMethod.GET,
					new HttpEntity<>(buildHeaders()), Employee.class);

			LOGGER.info("Successful call to get employee by id: {} , status code: {}", employeeId,
					responseEntity.getStatusCode());

			if (responseEntity.getStatusCode() == HttpStatus.OK && null != responseEntity.getBody()) {
				return responseEntity.getBody();
			} else {
				throw new EmployeeNotFoundException("Employee not found with id: " + employeeId);
			}
		} catch (RestClientException e) {
			LOGGER.error("Caught RestClientException while fetching the employee by id: {}, error: {}", employeeId,
					e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}
	}

	public Employee getEmployeeByName(String name) {

		String resourcePath = employeeServiceURL + GET_EMPLOYEE_BY_NAME_URL;

		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		LOGGER.info("Calling the get employee by name endpoint");

		try {
			ResponseEntity<Employee> responseEntity = restTemplate.exchange(resourcePath, HttpMethod.GET,
					new HttpEntity<>(buildHeaders()), Employee.class, params);

			LOGGER.info("Successful call to get employee by name: {}, status code: {}", name,
					responseEntity.getStatusCode());

			if (responseEntity.getStatusCode() == HttpStatus.OK && null != responseEntity.getBody()) {
				return responseEntity.getBody();
			} else {
				throw new EmployeeNotFoundException("Employee not found with name: " + name);
			}
		} catch (RestClientException e) {
			LOGGER.error("Caught RestClientException while fetching the employee by name: {}, error: {}", name,
					e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}
	}

	public ResponseStatus addEmployee(Employee employee) {

		String resourcePath = employeeServiceURL + ADD_EMPLOYEE_URL;
		HttpEntity<Employee> entity = new HttpEntity<>(employee, buildHeaders());

		LOGGER.info("Calling the add employee endpoint");
		try {

			ResponseEntity<ResponseStatus> responseEntity = restTemplate.exchange(resourcePath, HttpMethod.POST, entity,
					ResponseStatus.class);

			LOGGER.info("Successful call to add employee, status code: {}", responseEntity.getStatusCode());

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} else {
				return new ResponseStatus(String.valueOf(responseEntity.getStatusCode()), null,
						"Failed to add employee");
			}

		} catch (RestClientException e) {
			LOGGER.error("Caugh RestClientException while adding the employee, error: {}", e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}
	}

	public ResponseStatus addEmployees(List<Employee> employees) {
		String resourcePath = employeeServiceURL + ADD_EMPLOYEES_LIST_URL;
		HttpEntity<List<Employee>> entity = new HttpEntity<>(employees, buildHeaders());

		LOGGER.info("Calling the add employees list endpoint");
		try {

			ResponseEntity<ResponseStatus> responseEntity = restTemplate.exchange(resourcePath, HttpMethod.POST, entity,
					ResponseStatus.class);

			LOGGER.info("Successful call to add employee list, status code: {}", responseEntity.getStatusCode());

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} else {
				return new ResponseStatus(String.valueOf(responseEntity.getStatusCode()), null,
						"Failed to add employees list");
			}

		} catch (RestClientException e) {
			LOGGER.error("Caugh RestClientException while adding the employees list, error: {}", e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}
	}

	public ResponseStatus updateEmployee(Employee employee) {
		String resourcePath = employeeServiceURL + UPDATE_EMPLOYEE_URL;
		HttpEntity<Employee> entity = new HttpEntity<>(employee, buildHeaders());

		LOGGER.info("Calling the update employee endpoint");
		try {

			ResponseEntity<ResponseStatus> responseEntity = restTemplate.exchange(resourcePath, HttpMethod.PUT, entity,
					ResponseStatus.class);

			LOGGER.info("Successful call to update employee, status code: {}", responseEntity.getStatusCode());

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} else {
				return new ResponseStatus(String.valueOf(responseEntity.getStatusCode()), null,
						"Failed to update employee");
			}

		} catch (RestClientException e) {
			LOGGER.error("Caugh RestClientException while updating the employee, error: {}", e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}
	}

	public ResponseStatus deleteEmployee(int employeeId) {
		URI uri = UriComponentsBuilder.fromUriString(employeeServiceURL + DELETE_EMPLOYEE_URL)
				.buildAndExpand(String.valueOf(employeeId)).toUri();

		LOGGER.info("Calling the delete employee by id endpoint with uri: {}", uri.getRawPath());

		try {
			ResponseEntity<ResponseStatus> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE,
					new HttpEntity<>(buildHeaders()), ResponseStatus.class);

			LOGGER.info("Successful call to delete employee by id: {} , status code: {}", employeeId,
					responseEntity.getStatusCode());

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} else {
				return new ResponseStatus(String.valueOf(responseEntity.getStatusCode()), null,
						"Failed to delete employee");
			}
		} catch (RestClientException e) {
			LOGGER.error("Caught RestClientException while deleting the employee by id: {}, error: {}", employeeId,
					e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}
	}

	public ResponseStatus deleteAll() {
		LOGGER.info("Calling the delete all employees endpoint");

		try {
			ResponseEntity<ResponseStatus> responseEntity = restTemplate.exchange(
					employeeServiceURL + DELETE_ALL_EMPLOYEES_URL, HttpMethod.DELETE, new HttpEntity<>(buildHeaders()),
					ResponseStatus.class);

			LOGGER.info("Successful call to delete all employees, status code: {}", responseEntity.getStatusCode());

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} else {
				return new ResponseStatus(String.valueOf(responseEntity.getStatusCode()), null,
						"Failed to delete all employees");
			}
		} catch (RestClientException e) {
			LOGGER.error("Caught RestClientException while deleting all employees, error: {}", e.getMessage());
			throw new ClientException(e, EmployeeClient.class);
		}
	}

}
