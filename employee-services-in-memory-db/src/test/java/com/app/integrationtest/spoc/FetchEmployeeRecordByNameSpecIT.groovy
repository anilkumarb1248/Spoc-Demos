package com.app.integrationtest.spoc


import com.app.model.ApiResponse
import com.app.model.Employee
import groovy.json.JsonSlurper
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import spock.lang.Shared

class FetchEmployeeRecordByNameSpecIT extends BaseIntegrationSpecIT{

    @Shared
    String FETCH_EMPLOYEE_RECORD_BY_NAME_RESOURCE = "/employee/getByName"

    def "Should return employee record by name successfully with status OK"(){
        setup:
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>()
        queryParams.add("name", "Anil")
        URI uri = buildUri(FETCH_EMPLOYEE_RECORD_BY_NAME_RESOURCE,queryParams)

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity = callService(uri, HttpMethod.GET, null)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        ApiResponse apiResponse = new JsonSlurper().parseText(responseEntity.getBody())
        apiResponse
        apiResponse.status.code == "200"
        apiResponse.status.message == "Found employee details by name: Anil"
        apiResponse.payload
        Map employee = apiResponse.payload
        employee.empId == 1
        employee.empName == "Anil"
        employee.salary == 10000
        employee.mobileNumber == "123456789"
        employee.dateOfBirth == "1990-05-11"
        employee.email == "anil@gmail.com"
    }

    def "Should return no record by name with status NOT_FOUND"(){
        setup:
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>()
        queryParams.add("name", "Anil1234")
        URI uri = buildUri(FETCH_EMPLOYEE_RECORD_BY_NAME_RESOURCE,queryParams)

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity = callService(uri, HttpMethod.GET, null)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        ApiResponse apiResponse = new JsonSlurper().parseText(responseEntity.getBody())
        apiResponse
        apiResponse.error
        apiResponse.error.errorId == "404"
        apiResponse.error.errorMsg == "No employee found with firstName: Anil1234"
        apiResponse.error.errorDesc == "Get employee by name failed"
    }
}
