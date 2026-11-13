package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.CustomerServicePort;
import com.jardineria.domain.model.Customer;
import com.jardineria.domain.ports.out.CustomerRepository;

import java.util.List;
import java.util.Optional;

public class CustomerUseCase implements CustomerServicePort {

    private final CustomerRepository customerRepository;

    public CustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Integer customerCode) {
        return customerRepository.findById(customerCode);
    }

    @Override
    public void update(Customer customer) {
        customerRepository.update(customer);
    }

    @Override
    public void deleteById(Integer customerCode) {
        customerRepository.deleteById(customerCode);
    }
}
