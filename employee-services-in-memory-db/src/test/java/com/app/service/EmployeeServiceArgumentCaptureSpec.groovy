package com.app.service

import com.app.entity.EmployeeEntity
import com.app.exceptions.DuplicateEmployeeException
import com.app.exceptions.EmployeeNotFoundException
import com.app.model.ApiResponse
import com.app.model.Employee
import com.app.repository.EmployeeRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.tomcat.jni.Local
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EmployeeServiceArgumentCaptureSpec extends Specification {

    def employeeRepository = Mock(EmployeeRepository)

    @Subject
    def employeeService = new EmployeeServiceImpl(employeeRepository)

    // We are passing Employee model to service.addEmployee() method, but internally we are creating
    // EmployeeEntity object to pass to the repository.
    // We need to make sure we are passing correct values in EmployeeEntity
    // And also it helps in code refactoring in near future
    def "addEmployee method argument capture test case"(){
        given:
        LocalDate dob = LocalDate.parse("1990-May-11", DateTimeFormatter.ofPattern("yyyy-MMM-dd"))

        def mockEmployee = new Employee(empId: 100, empName: "testName", role: "testRole", salary: 10000,
                mobileNumber: "1234567890", email: "abc@test.com",
                dateOfBirth: dob)
        def emptyOptional = Optional.empty()
        EmployeeEntity employeeEntity = null

        ObjectMapper objectMapper = new ObjectMapper()
        println(objectMapper.writeValueAsString(mockEmployee))

        when:
        employeeService.addEmployee(mockEmployee)

        then:
        1 * employeeRepository.findByEmpName(_ as String) >> emptyOptional
        1 * employeeRepository.save(_ as EmployeeEntity) >> {arguments -> employeeEntity = arguments[0]}

        expect:
        employeeEntity
        employeeEntity.empId == 100
        employeeEntity.empName == "testName"
        employeeEntity.role == "testRole"
        employeeEntity.salary == 10000
        employeeEntity.mobileNumber == "1234567890"
        employeeEntity.email == "abc@test.com"
        employeeEntity.dateOfBirth == dob
    }

}
