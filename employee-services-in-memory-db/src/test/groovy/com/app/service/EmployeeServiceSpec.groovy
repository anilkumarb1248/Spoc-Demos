package com.app.service

import com.app.entity.EmployeeEntity
import com.app.model.ApiResponse
import com.app.model.Employee
import com.app.repository.EmployeeRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class EmployeeServiceSpec extends Specification{

    def employeeRepository = Mock(EmployeeRepository.class)

    @Subject
    def employeeService = new EmployeeServiceImpl(employeeRepository)

    @Shared
    def mockEmployee = new Employee(empId: 100,empName: "testName", role: "testRole", salary: 10000, mobileNumber: "1234567890", email: "abc@test.com", dateOfBirth: LocalDate.now())

    @Shared
    def mockEmployeeEntity = new EmployeeEntity(empId: 100,empName: "testName", role: "testRole", salary: 10000, mobileNumber: "1234567890", email: "abc@test.com", dateOfBirth: LocalDate.now())


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

    def "getEmployeeList - Verify successful operation to fetch all employees"(){
        given:
        List mockResponse = new ArrayList()
        mockResponse.add(mockEmployeeEntity)

        when:
        List<Employee> employeesList =  employeeService.getEmployeeList()

        then:
        1 * employeeRepository.findAll() >> mockResponse

        expect:
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
}
