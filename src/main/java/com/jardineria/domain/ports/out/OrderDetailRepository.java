package com.jardineria.domain.ports.out;

import com.jardineria.domain.model.OrderDetail;
import com.jardineria.domain.model.OrderDetailId;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, OrderDetailId> {
    Optional<OrderDetail> findById(Integer orderCode, String productCode);
    void deleteById(Integer orderCode, String productCode);
    List<OrderDetail> findByOrderCode(Integer orderCode);
}
