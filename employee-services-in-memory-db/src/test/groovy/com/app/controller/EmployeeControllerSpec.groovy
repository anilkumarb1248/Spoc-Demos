package com.app.controller

import com.app.exceptions.DuplicateEmployeeException
import com.app.exceptions.EmployeeNotFoundException
import com.app.model.ApiResponse
import com.app.model.Employee
import com.app.model.Error
import com.app.model.Status
import com.app.service.EmployeeServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate

class EmployeeControllerSpec extends Specification{

    def employeeService = Mock(EmployeeServiceImpl.class)

    @Subject
    def employeeController = new EmployeeControllerImpl(employeeService)

    @Shared
    Employee mockEmployee = new Employee(empId: 100,empName: "testName", role: "testRole", salary: 10000, mobileNumber: "1234567890", email: "abc@test.com", dateOfBirth: LocalDate.now())

    // Common helper method to assert the employee details (mockEmployee) from different feature methods
    void assertEmployeeDetails(employee){
        assert employee.empId == 100
        assert employee.empName == "testName"
        assert employee.role == "testRole"
        assert employee.salary == 10000
        assert employee.mobileNumber == "1234567890"
        assert employee.email == "abc@test.com"
        assert employee.dateOfBirth == LocalDate.now()
    }

    def "getEmployeeList - verify successful operation to fetch all employees"(){
        given:
        List mockResponse = new ArrayList()
        mockResponse.add(mockEmployee)

        when:
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity =  employeeController.getEmployeeList()

        then:
        1 * employeeService.getEmployeeList() >> mockResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().payload
        def employeesList = responseEntity.getBody().payload
        employeesList
        employeesList.size() == 1

//        employeesList[0].empId == 1
//        employeesList[0].empName == "testName"
//        employeesList[0].role == "testRole"
//        employeesList[0].salary == 10000
//        employeesList[0].mobileNumber == "1234567890"
//        employeesList[0].email == "abc@test.com"
//        employeesList[0].dateOfBirth == LocalDate.now()

//        Note: We can move the above assertions to Helper method, so that we can use the helper method in multiple features

        assertEmployeeDetails(employeesList[0])
    }

    @Unroll
    def "Fetch all employees failure scenario when service returns #condition"(){
        when:
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity =  employeeController.getEmployeeList()

        then:
        1 * employeeService.getEmployeeList() >> response

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "404"
        responseEntity.getBody().error.errorMsg == "Employee table is empty"
        responseEntity.getBody().error.errorDesc == "NOT_FOUND"

        where:
        condition | response
        "null" | null
        "empty" | new ArrayList<>()
    }

    def "getEmployee success scenario"(){
        given:
        def employeeId = "100"
        ApiResponse<Employee> apiResponse = new ApiResponse<>()
        apiResponse.setPayload(mockEmployee)

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.getEmployee(employeeId)

        then:
        1 * employeeService.getEmployee(employeeId) >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().payload
//        Making the assertions in the helper method
        assertEmployeeDetails(responseEntity.getBody().payload)

    }

    def "getEmployee failure scenario when service throws not found exception"(){
        given:
        def employeeId = "100"

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.getEmployee(employeeId)

        then:
        1 * employeeService.getEmployee(employeeId) >> {throw new EmployeeNotFoundException("Employee not found with given id")}

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "404"
        responseEntity.getBody().error.errorMsg == "Employee not found with given id"
        responseEntity.getBody().error.errorDesc == "Get employee by id failed"
    }

    def "getEmployee bad request scenario when employee id: #condition"(){

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.getEmployee(employeeId)

        then:
        0 * employeeService.getEmployee(employeeId)

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "400"
        responseEntity.getBody().error.errorMsg == "Id should not be blank or non numeric"
        responseEntity.getBody().error.errorDesc == "BAD_REQUEST"

        where:
        condition | employeeId
        "empty" | "  "
        "null" | null
        "non numeric" | "abcd"
    }

    def "getEmployeeByName success scenario"(){
        given:
        def name = "testName"
        ApiResponse<Employee> apiResponse = new ApiResponse<>()
        apiResponse.setPayload(mockEmployee)

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.getEmployeeByName(name)

        then:
        1 * employeeService.getEmployeeByName(name) >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().payload
//        Making the assertions in the helper method
        assertEmployeeDetails(responseEntity.getBody().payload)

    }

    def "getEmployeeByName failure scenario when service throws not found exception"(){
        given:
        def name = "testName"

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.getEmployeeByName(name)

        then:
        1 * employeeService.getEmployeeByName(name) >> {throw new EmployeeNotFoundException("Employee not found with given name")}

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "404"
        responseEntity.getBody().error.errorMsg == "Employee not found with given name"
        responseEntity.getBody().error.errorDesc == "Get employee by name failed"
    }

    def "getEmployeeByName bad request scenario when employee id: #condition"(){

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.getEmployeeByName(name)

        then:
        0 * employeeService.getEmployeeByName(name)

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "400"
        responseEntity.getBody().error.errorMsg == "Name should not be blank"
        responseEntity.getBody().error.errorDesc == "BAD_REQUEST"

        where:
        condition | name
        "empty" | "  "
        "null" | null
    }

    def "addEmployee success scenario"(){
        given:
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(new Status("201","Employee added successfully"));

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.addEmployee(mockEmployee)

        then:
        1 * employeeService.addEmployee(mockEmployee) >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.CREATED
        responseEntity.getBody()
        responseEntity.getBody().status
        responseEntity.getBody().status.code == "201"
        responseEntity.getBody().status.message == "Employee added successfully"
    }

    def "addEmployee failure scenario when service returns duplicate employee exception"(){
        given:
        def duplicateException = new DuplicateEmployeeException("Duplicate employee details")

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.addEmployee(mockEmployee)

        then:
        1 * employeeService.addEmployee(mockEmployee) >> {throw duplicateException}

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.CONFLICT
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "409"
        responseEntity.getBody().error.errorMsg == "Duplicate employee details"
        responseEntity.getBody().error.errorDesc == "CONFLICT"
    }

    def "updateEmployee success scenario"(){
        given:
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(new Status("200","Employee updated successfully"));

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.updateEmployee(mockEmployee)

        then:
        1 * employeeService.updateEmployee(mockEmployee) >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().status
        responseEntity.getBody().status.code == "200"
        responseEntity.getBody().status.message == "Employee updated successfully"
    }

    def "updateEmployee failure scenario when service return employee not found exception"(){
        given:
        def employeeNotFoundException = new EmployeeNotFoundException("Employee not found with given details")

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.updateEmployee(mockEmployee)

        then:
        1 * employeeService.updateEmployee(mockEmployee) >> {throw employeeNotFoundException}

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.FAILED_DEPENDENCY
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "424"
        responseEntity.getBody().error.errorMsg == "Employee not found with given details"
        responseEntity.getBody().error.errorDesc == "FAILED_DEPENDENCY"
    }

    def "deleteEmployee success scenario"(){
        given:
        def employeeId = "100"
        ApiResponse<Employee> apiResponse = new ApiResponse<>()
        apiResponse.setPayload(mockEmployee)

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.deleteEmployee(employeeId)

        then:
        1 * employeeService.deleteEmployee(employeeId) >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().payload
//        Making the assertions in the helper method
        assertEmployeeDetails(responseEntity.getBody().payload)

    }

    def "deleteEmployee failure scenario when service throws not found exception"(){
        given:
        def employeeId = "100"

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.deleteEmployee(employeeId)

        then:
        1 * employeeService.deleteEmployee(employeeId) >> {throw new EmployeeNotFoundException("Employee not found with given id")}

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "404"
        responseEntity.getBody().error.errorMsg == "Employee not found with given id"
        responseEntity.getBody().error.errorDesc == "Delete employee by id failed"
    }

    def "deleteEmployee bad request scenario when employee id: #condition"(){

        when:
        ResponseEntity<ApiResponse<Employee>> responseEntity =  employeeController.deleteEmployee(employeeId)

        then:
        0 * employeeService.deleteEmployee(employeeId)

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "400"
        responseEntity.getBody().error.errorMsg == "Id should not be blank or non numeric"
        responseEntity.getBody().error.errorDesc == "BAD_REQUEST"

        where:
        condition | employeeId
        "empty" | "  "
        "null" | null
        "non numeric" | "abcd"
    }

    def "verify addEmployees method call"(){
        given:
        ApiResponse<Employee> apiResponse = new ApiResponse<>()
        apiResponse.setStatus(new Status("201","Employees list added successfully"))

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.addEmployees(new ArrayList<Employee>())

        then:
        1 * employeeService.addEmployees(_) >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.CREATED
        responseEntity.getBody()
        responseEntity.getBody().status
        responseEntity.getBody().status.code == "201"
        responseEntity.getBody().status.message == "Employees list added successfully"
    }

    def "verify deleteAll method call"(){
        given:
        ApiResponse<Employee> apiResponse = new ApiResponse<>()
        apiResponse.setStatus(new Status("200","Successfully deleted all records"))

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.deleteAll()

        then:
        1 * employeeService.deleteAll() >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().status
        responseEntity.getBody().status.code == "200"
        responseEntity.getBody().status.message == "Successfully deleted all records"
    }

    def "verify addDummyData success scenario"(){
        given:
        ApiResponse<Employee> apiResponse = new ApiResponse<>()
        apiResponse.setStatus(new Status("200","Dummy data added successfully"))

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.addDummyData()

        then:
        1 * employeeService.addDummyData() >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().status
        responseEntity.getBody().status.code == "200"
        responseEntity.getBody().status.message == "Dummy data added successfully"
    }

    def "verify addDummyData failure scenario"(){
        given:
        ApiResponse<Employee> apiResponse = new ApiResponse<>()
        apiResponse.setError(new Error("500", "Failed to add dummy data", "Internal Server error"))

        when:
        ResponseEntity<ApiResponse> responseEntity =  employeeController.addDummyData()

        then:
        1 * employeeService.addDummyData() >> apiResponse

        expect:
        responseEntity
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody()
        responseEntity.getBody().error
        responseEntity.getBody().error.errorId == "500"
        responseEntity.getBody().error.errorMsg == "Failed to add dummy data"
        responseEntity.getBody().error.errorDesc == "Internal Server error"
    }
}
