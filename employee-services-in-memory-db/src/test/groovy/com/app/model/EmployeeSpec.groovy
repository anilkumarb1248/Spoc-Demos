package com.app.model

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EmployeeSpec extends Specification{

    def "jackson deserialization works when object is deserialized"(){
        setup:
        def jsonInput = '{"empId":100,"empName":"testName","role":"testRole","salary":10000.0,"dateOfBirth":"1990-05-11","mobileNumber":"1234567890","email":"abc@test.com"}'
        
        when:
        Employee employee = new ObjectMapper().readValue(jsonInput, Employee.class)
        
        then:
        employee
        employee.empId == 100
        employee.empName == "testName"
        employee.role == "testRole"
        employee.salary == 10000
        employee.mobileNumber == "1234567890"
        employee.email == "abc@test.com"
        employee.dateOfBirth.format(DateTimeFormatter.ofPattern("yyyy-MMM-dd")) == "1990-May-11"

    }

    def "jackson serialization works when object is serialized"(){
        given:
        LocalDate dob = LocalDate.parse("1990-May-11", DateTimeFormatter.ofPattern("yyyy-MMM-dd"))

        def employee = new Employee(empId: 100, empName: "testName", role: "testRole", salary: 10000,
                mobileNumber: "1234567890", email: "abc@test.com",
                dateOfBirth: dob)

        def expectedString = '{"empId":100,"empName":"testName","role":"testRole","salary":10000.0,"dateOfBirth":"1990-05-11","mobileNumber":"1234567890","email":"abc@test.com"}'

        when:
        String serializedRequest = new ObjectMapper().writeValueAsString(employee)

        then:
        serializedRequest
        serializedRequest == expectedString
    }
}
