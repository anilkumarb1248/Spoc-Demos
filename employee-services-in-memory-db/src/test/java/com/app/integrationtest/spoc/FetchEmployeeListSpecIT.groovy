package com.app.integrationtest.spoc

import com.app.model.ApiResponse
import com.app.model.Employee
import groovy.json.JsonSlurper
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared

class FetchEmployeeListSpecIT extends BaseIntegrationSpecIT{

    @Shared
    String FETCH_EMPLOYEES_LIST_RESOURCE = "/employee/list"

    def "Should return employee records list successfully"(){
        setup:
        URI uri = buildUri(FETCH_EMPLOYEES_LIST_RESOURCE)

        when:
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = callService(uri, HttpMethod.GET, null)

        then:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        ApiResponse apiResponse = new JsonSlurper().parseText(responseEntity.getBody())
        apiResponse
        apiResponse.status.code == "200"
        apiResponse.status.message == "Returning employee records successfully"
        apiResponse.payload
        List employeesList = apiResponse.payload
        employeesList.size() == 15
    }
}
