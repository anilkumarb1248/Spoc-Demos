package com.app.integrationtest.spoc


import com.app.model.ApiResponse
import com.app.model.Employee
import groovy.json.JsonSlurper
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared

class FetchEmployeeRecordByIdSpecIT extends BaseIntegrationSpecIT{

    @Shared
    String FETCH_EMPLOYEE_RECORD_BY_ID_RESOURCE = "/employee/getById/"

    def "Should return employee record by id successfully with status OK"(){
        setup:
        String employeeId = "1"
        URI uri = buildUri(FETCH_EMPLOYEE_RECORD_BY_ID_RESOURCE + employeeId)

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity = callService(uri, HttpMethod.GET, null)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        ApiResponse apiResponse = new JsonSlurper().parseText(responseEntity.getBody())
        apiResponse
        apiResponse.status.code == "200"
        apiResponse.status.message == "Found employee details by Id: 1"
        apiResponse.payload
        Map employee = apiResponse.payload
        employee.empId == 1
        employee.empName == "Anil"
        employee.salary == 10000
        employee.mobileNumber == "123456789"
        employee.dateOfBirth == "1990-05-11"
        employee.email == "anil@gmail.com"
    }

    def "Should return no record by id with status NOT_FOUND"(){
        setup:
        String employeeId = "100"
        URI uri = buildUri(FETCH_EMPLOYEE_RECORD_BY_ID_RESOURCE + employeeId)

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity = callService(uri, HttpMethod.GET, null)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        ApiResponse apiResponse = new JsonSlurper().parseText(responseEntity.getBody())
        apiResponse
        apiResponse.error
        apiResponse.error.errorId == "404"
        apiResponse.error.errorMsg == "No employee found with id: 100"
        apiResponse.error.errorDesc == "Get employee by id failed"
    }
}
