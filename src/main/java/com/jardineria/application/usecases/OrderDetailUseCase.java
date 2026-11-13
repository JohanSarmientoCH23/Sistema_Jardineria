package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.OrderDetailServicePort;
import com.jardineria.domain.model.OrderDetail;
import com.jardineria.domain.model.OrderDetailId;
import com.jardineria.domain.ports.out.OrderDetailRepository;

import java.util.List;
import java.util.Optional;

public class OrderDetailUseCase implements OrderDetailServicePort {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailUseCase(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public void save(OrderDetail orderDetail) {
        orderDetailRepository.save(orderDetail);
    }

    @Override
    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }

    @Override
    public Optional<OrderDetail> findById(OrderDetailId id) {
        return orderDetailRepository.findById(id);
    }

    @Override
    public Optional<OrderDetail> findById(Integer orderCode, String productCode) {
        return orderDetailRepository.findById(orderCode, productCode);
    }

    @Override
    public void update(OrderDetail orderDetail) {
        orderDetailRepository.update(orderDetail);
    }

    @Override
    public void deleteById(OrderDetailId id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public void deleteById(Integer orderCode, String productCode) {
        orderDetailRepository.deleteById(orderCode, productCode);
    }

    @Override
    public List<OrderDetail> findByOrderCode(Integer orderCode) {
        return orderDetailRepository.findByOrderCode(orderCode);
    }
}
