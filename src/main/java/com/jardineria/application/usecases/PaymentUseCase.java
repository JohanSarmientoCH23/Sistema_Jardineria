package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.PaymentServicePort;
import com.jardineria.domain.model.Payment;
import com.jardineria.domain.model.PaymentId;
import com.jardineria.domain.ports.out.PaymentRepository;

import java.util.List;
import java.util.Optional;

public class PaymentUseCase implements PaymentServicePort {

    private final PaymentRepository paymentRepository;

    public PaymentUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Optional<Payment> findById(Integer customerCode, String transactionId) {
        return paymentRepository.findById(customerCode, transactionId);
    }

    @Override
    public void update(Payment payment) {
        paymentRepository.update(payment);
    }

    @Override
    public void deleteById(PaymentId id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public void deleteById(Integer customerCode, String transactionId) {
        paymentRepository.deleteById(customerCode, transactionId);
    }

    @Override
    public List<Payment> findByCustomerCode(Integer customerCode) {
        return paymentRepository.findByCustomerCode(customerCode);
    }
}
