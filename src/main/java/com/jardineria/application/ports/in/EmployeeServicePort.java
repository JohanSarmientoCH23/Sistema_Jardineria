package com.jardineria.application.ports.in;

import com.jardineria.domain.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeServicePort {
    void save(Employee employee);
    List<Employee> findAll();
    Optional<Employee> findById(Integer employeeCode);
    void update(Employee employee);
    void deleteById(Integer employeeCode);
}
