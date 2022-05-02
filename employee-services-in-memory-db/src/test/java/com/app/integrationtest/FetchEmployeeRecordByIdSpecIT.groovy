package com.app.integrationtest

import com.app.BaseIntegrationSpecIT
import com.app.model.ApiResponse
import com.app.model.Employee
import org.springframework.http.ResponseEntity
import spock.lang.Shared

class FetchEmployeeRecordByIdSpecIT extends BaseIntegrationSpecIT{

    @Shared
    String EMPLOYEE_RESOURCE = "/employee"

    @Shared
    String FETCH_EMPLOYEE_RECORD_BY_ID_RESOURCE = EMPLOYEE_RESOURCE + "/getById"

    def "Should return employee record successfully with status OK"(){
        setup:
        String employeeId = "1"

        when:
        println("******************** 1")
        URI uri = buildUri(FETCH_EMPLOYEE_RECORD_BY_ID_RESOURCE +"/" + employeeId)
        println("******************** 2")
        println(uri)
        ResponseEntity<ApiResponse<Employee>> responseEntity = callService(uri, HttpMethod.GET, null)

        then:
        responseEntity
    }

    def "Should return no record with status NOT_FOUND"(){

    }
}
