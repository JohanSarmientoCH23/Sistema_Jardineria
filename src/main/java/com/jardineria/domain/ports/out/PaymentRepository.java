package com.jardineria.domain.ports.out;

import com.jardineria.domain.model.Payment;
import com.jardineria.domain.model.PaymentId;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, PaymentId> {
    Optional<Payment> findById(Integer customerCode, String transactionId);
    void deleteById(Integer customerCode, String transactionId);
    List<Payment> findByCustomerCode(Integer customerCode);
}
