package com.jardineria.application.ports.in;

import com.jardineria.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderServicePort {
    void save(Order order);
    List<Order> findAll();
    Optional<Order> findById(Integer orderCode);
    void update(Order order);
    void deleteById(Integer orderCode);
}
