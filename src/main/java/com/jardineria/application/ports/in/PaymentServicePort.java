package com.jardineria.application.ports.in;

import com.jardineria.domain.model.Payment;
import com.jardineria.domain.model.PaymentId;

import java.util.List;
import java.util.Optional;

public interface PaymentServicePort {
    void save(Payment payment);
    List<Payment> findAll();
    Optional<Payment> findById(PaymentId id);
    Optional<Payment> findById(Integer customerCode, String transactionId);
    void update(Payment payment);
    void deleteById(PaymentId id);
    void deleteById(Integer customerCode, String transactionId);
    List<Payment> findByCustomerCode(Integer customerCode);
}
