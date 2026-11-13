package com.jardineria.application.ports.in;

import com.jardineria.domain.model.OrderDetail;
import com.jardineria.domain.model.OrderDetailId;

import java.util.List;
import java.util.Optional;

public interface OrderDetailServicePort {
    void save(OrderDetail orderDetail);
    List<OrderDetail> findAll();
    Optional<OrderDetail> findById(OrderDetailId id);
    Optional<OrderDetail> findById(Integer orderCode, String productCode);
    void update(OrderDetail orderDetail);
    void deleteById(OrderDetailId id);
    void deleteById(Integer orderCode, String productCode);
    List<OrderDetail> findByOrderCode(Integer orderCode);
}
