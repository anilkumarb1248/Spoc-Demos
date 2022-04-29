package com.app.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entity.EmployeeEntity;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

	Optional<EmployeeEntity> findByEmpName(String name);

	List<EmployeeEntity> findBySalaryBetween(double start, double end);

	List<EmployeeEntity> findByMobileNumberStartingWith(String start);

	List<EmployeeEntity> findByDateOfBirthBefore(Date date);

	List<EmployeeEntity> findBySalaryGreaterThanEqual(double salary);

	List<EmployeeEntity> findBySalaryLessThan(double salary);
}
