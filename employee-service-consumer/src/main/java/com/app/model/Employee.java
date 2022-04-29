package com.app.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	private int empId;
	private String empName;
	private String role;
	private double salary;
	private Date dateOfBirth;
	private String mobileNumber;
	private String email;
}
