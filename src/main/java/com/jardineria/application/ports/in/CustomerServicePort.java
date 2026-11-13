package com.jardineria.application.ports.in;

import com.jardineria.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerServicePort {
    void save(Customer customer);
    List<Customer> findAll();
    Optional<Customer> findById(Integer customerCode);
    void update(Customer customer);
    void deleteById(Integer customerCode);
}
