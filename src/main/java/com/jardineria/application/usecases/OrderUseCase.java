package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.OrderServicePort;
import com.jardineria.domain.model.Order;
import com.jardineria.domain.ports.out.OrderRepository;

import java.util.List;
import java.util.Optional;

public class OrderUseCase implements OrderServicePort {

    private final OrderRepository orderRepository;

    public OrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Integer orderCode) {
        return orderRepository.findById(orderCode);
    }

    @Override
    public void update(Order order) {
        orderRepository.update(order);
    }

    @Override
    public void deleteById(Integer orderCode) {
        orderRepository.deleteById(orderCode);
    }
}
