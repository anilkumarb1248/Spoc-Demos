package com.app.service

import com.app.entity.EmployeeEntity
import com.app.exceptions.DuplicateEmployeeException
import com.app.exceptions.EmployeeNotFoundException
import com.app.model.ApiResponse
import com.app.model.Employee
import com.app.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate

class EmployeeServiceSpec extends Specification {

    def employeeRepository = Mock(EmployeeRepository)

    @Subject
    def employeeService = new EmployeeServiceImpl(employeeRepository)

/**
 // Note: If we are using property injection (without constructor injection), we can mock as below
 def employeeRepository = Mock(EmployeeRepository)
 def employeeService = new EmployeeServiceImpl()
 def setup(){employeeService.employeeRepository = employeeRepository;}*/


    @Shared
    def mockEmployee = new Employee(empId: 100, empName: "testName", role: "testRole", salary: 10000, mobileNumber: "1234567890", email: "abc@test.com", dateOfBirth: LocalDate.now())

    @Shared
    def mockEmployeeEntity = new EmployeeEntity(empId: 100, empName: "testName", role: "testRole", salary: 10000, mobileNumber: "1234567890", email: "abc@test.com", dateOfBirth: LocalDate.now())


// Common helper method to assert the employee details (mockEmployee) from different feature methods
    void assertEmployeeDetails(employee) {
        assert employee.empId == 100
        assert employee.empName == "testName"
        assert employee.role == "testRole"
        assert employee.salary == 10000
        assert employee.mobileNumber == "1234567890"
        assert employee.email == "abc@test.com"
        assert employee.dateOfBirth == LocalDate.now()
    }

    def "getEmployeeList - successful operation to fetch all employees"() {
        given:
        List mockResponse = new ArrayList()
        mockResponse.add(mockEmployeeEntity)

        when:
        List<Employee> employeesList = employeeService.getEmployeeList()

        then:
        1 * employeeRepository.findAll() >> mockResponse

        expect:
        employeesList
        employeesList.size() == 1
//        employeesList[0].empId == 100
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
    def "getEmployeeList - failure scenario when repository returns #condition"() {
        given:
        def returnedListSize = 0

        when:
        List<Employee> employeesList = employeeService.getEmployeeList()

        then:
        1 * employeeRepository.findAll() >> mockResponse

        expect:
        employeesList.size() == returnedListSize

        where:
        condition    | mockResponse
        "null"       | null
        "empty list" | new ArrayList()
    }

    def "getEmployee - get employee by id success scenario"() {
        given:
        def employeeId = "100"
        Optional<EmployeeEntity> optionalEntity = Optional.of(mockEmployeeEntity)

        when:
        ApiResponse<Employee> apiResponse = employeeService.getEmployee(employeeId)

        then:
        1 * employeeRepository.findById(Integer.valueOf(employeeId)) >> optionalEntity

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "200"
        apiResponse.status.message == "Found employee details by Id: " + employeeId
        apiResponse.payload
        //Making the assertions in the helper method
        assertEmployeeDetails(apiResponse.payload)
    }

    def "getEmployee - throws no employee found exception when no records found"() {
        given:
        def employeeId = "100"
        Optional<EmployeeEntity> optionalEntity = Optional.empty()

        when:
        employeeService.getEmployee(employeeId)

        then:
        1 * employeeRepository.findById(Integer.valueOf(employeeId)) >> optionalEntity

        and:
        thrown(EmployeeNotFoundException)
    }

    def "getEmployeeByName - get employee by name success scenario"() {
        given:
        def name = "testName"
        Optional<EmployeeEntity> optionalEntity = Optional.of(mockEmployeeEntity)

        when:
        ApiResponse<Employee> apiResponse = employeeService.getEmployeeByName(name)

        then:
        1 * employeeRepository.findByEmpName(name) >> optionalEntity

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "200"
        apiResponse.status.message == "Found employee details by name: " + name
        apiResponse.payload
        //Making the assertions in the helper method
        assertEmployeeDetails(apiResponse.payload)
    }

    def "getEmployeeByName - throws no employee found exception when no records found"() {
        given:
        def name = "Anil"
        Optional<EmployeeEntity> optionalEntity = Optional.empty()

        when:
        employeeService.getEmployeeByName(name)

        then:
        1 * employeeRepository.findByEmpName(name) >> optionalEntity

        and:
        thrown(EmployeeNotFoundException)
    }

    def "addEmployee - success scenario"(){
        given:
        def emptyOptional = Optional.empty()

        when:
        ApiResponse apiResponse = employeeService.addEmployee(mockEmployee)

        then:
        1 * employeeRepository.findByEmpName(_ as String) >> emptyOptional
        1 * employeeRepository.save(_ as EmployeeEntity) >> mockEmployeeEntity

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "201"
        apiResponse.status.message == "Employee added successfully"
    }

    def "addEmployee - failure scenario when failed to insert employee"(){
        given:
        def emptyOptional = Optional.empty()

        when:
        ApiResponse apiResponse = employeeService.addEmployee(mockEmployee)

        then:
        1 * employeeRepository.findByEmpName(_ as String) >> emptyOptional
        1 * employeeRepository.save(_ as EmployeeEntity) >> null

        expect:
        apiResponse
        apiResponse.error
        apiResponse.error.errorId == "500"
        apiResponse.error.errorMsg == "Failed to add Employee"
        apiResponse.error.errorDesc == "INTERNAL_SERVER_ERROR"
    }

    def "addEmployee - throws duplicate record exception"(){
        given:
        def optionalEnity = Optional.of(mockEmployeeEntity)

        when:
        employeeService.addEmployee(mockEmployee)

        then:
        1 * employeeRepository.findByEmpName(_ as String) >> optionalEnity
        0 * employeeRepository.save(_ as EmployeeEntity)

        and:
        thrown(DuplicateEmployeeException)
    }

    def "addEmployees - verify the add employees list method call"(){
        given:
        def employeesList = new ArrayList<Employee>()
        employeesList.add(mockEmployee)

        when:
        ApiResponse apiResponse = employeeService.addEmployees(employeesList)

        then:
        1 * employeeRepository.findByEmpName(_ as String) >> Optional.empty()
        1 * employeeRepository.saveAll(_ as List) >> null

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "201"
        apiResponse.status.message == "Employees added successfully"
    }

    def "updateEmployee - success scenario"(){
        given:
        def emptyOptional = Optional.empty()

        when:
        ApiResponse apiResponse = employeeService.updateEmployee(mockEmployee)

        then:
        1 * employeeRepository.existsById(mockEmployee.empId) >> true
        1 * employeeRepository.findByEmpName(_ as String) >> emptyOptional
        1 * employeeRepository.save(_ as EmployeeEntity) >> null

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "200"
        apiResponse.status.message == "Employee updated successfully"

    }

    def "updateEmployee - throw employee not found exception with given employee id"(){
        given:
        def isExistFlag = false

        when:
        employeeService.updateEmployee(mockEmployee)

        then:
        1 * employeeRepository.existsById(mockEmployee.empId) >> isExistFlag
        0 * employeeRepository.findByEmpName(_ as String)
        0 * employeeRepository.save(_ as EmployeeEntity)

        and:
        thrown(EmployeeNotFoundException)
    }

    def "updateEmployee - throws duplicate employee exception"(){
        given:
        def isExistFlag = true
        EmployeeEntity entity  = new EmployeeEntity(empId: 200, empName: "Anil")

        when:
        employeeService.updateEmployee(mockEmployee)

        then:
        1 * employeeRepository.existsById(mockEmployee.empId) >> isExistFlag
        1 * employeeRepository.findByEmpName(_ as String) >> Optional.of(entity)
        0 * employeeRepository.save(_ as EmployeeEntity)

        and:
        thrown(DuplicateEmployeeException)
    }

    def "deleteEmployee - success scenario"(){
        given:
        def employeeId = "100"

        when:
        ApiResponse apiResponse = employeeService.deleteEmployee(employeeId)

        then:
        1 * employeeRepository.existsById(Integer.valueOf(employeeId)) >> true
        1 * employeeRepository.deleteById(Integer.valueOf(employeeId))

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "200"
        apiResponse.status.message == "Employee deleted successfully"
    }

    def "deleteEmployee - throws no employee found exception when no record found"(){
        given:
        def employeeId = "100"

        when:
        employeeService.deleteEmployee(employeeId)

        then:
        1 * employeeRepository.existsById(Integer.valueOf(employeeId)) >> false
        0 * employeeRepository.deleteById(Integer.valueOf(employeeId))

        and:
        thrown(EmployeeNotFoundException)
    }

    def "deleteAll - success scenario"(){
        given:
        def enityList = new ArrayList<EmployeeEntity>()

        when:
        ApiResponse apiResponse = employeeService.deleteAll()

        then:
        1 * employeeRepository.findAll() >> enityList
        1 * employeeRepository.deleteAll(_ as List)

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "200"
        apiResponse.status.message == "Employees deleted successfully"
    }

    def "deleteAll - failure scenario"(){
        given:
        def enityList = new ArrayList<EmployeeEntity>()

        when:
        ApiResponse apiResponse = employeeService.deleteAll()

        then:
        1 * employeeRepository.findAll() >> enityList
        1 * employeeRepository.deleteAll(_ as List) >> {throw new RuntimeException("Internal Server Error")}

        expect:
        apiResponse
        apiResponse.error
        apiResponse.error.errorId == "500"
        apiResponse.error.errorMsg == "Failed to delete all employees"
        apiResponse.error.errorDesc == "Internal Server Error"
    }

    def "addDummyData - success scenario"(){
        given:
        def emptyOptional = Optional.empty()

        when:
        ApiResponse apiResponse = employeeService.addDummyData()

        then:
        15 * employeeRepository.findByEmpName(_ as String) >> emptyOptional
        1 * employeeRepository.saveAll(_ as List)

        expect:
        apiResponse
        apiResponse.status
        apiResponse.status.code == "200"
        apiResponse.status.message == "Inserted employees dummy data successfully"

    }

    def "addDummyData - failure scenario when internal server error occurred"(){
        given:
        def emptyOptional = Optional.empty()

        when:
        ApiResponse apiResponse = employeeService.addDummyData()

        then:
        15 * employeeRepository.findByEmpName(_ as String) >> emptyOptional
        1 * employeeRepository.saveAll(_ as List) >> {throw new RuntimeException("Internal Server Error")}

        expect:
        apiResponse
        apiResponse.error
        apiResponse.error.errorId == "500"
        apiResponse.error.errorMsg == "Failed to insert employee dummy data"
        apiResponse.error.errorDesc == "Internal Server Error"
    }
}
