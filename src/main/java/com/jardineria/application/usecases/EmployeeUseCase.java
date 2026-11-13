package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.EmployeeServicePort;
import com.jardineria.domain.model.Employee;
import com.jardineria.domain.ports.out.EmployeeRepository;

import java.util.List;
import java.util.Optional;

public class EmployeeUseCase implements EmployeeServicePort {

    private final EmployeeRepository employeeRepository;

    public EmployeeUseCase(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> findById(Integer employeeCode) {
        return employeeRepository.findById(employeeCode);
    }

    @Override
    public void update(Employee employee) {
        employeeRepository.update(employee);
    }

    @Override
    public void deleteById(Integer employeeCode) {
        employeeRepository.deleteById(employeeCode);
    }
}
