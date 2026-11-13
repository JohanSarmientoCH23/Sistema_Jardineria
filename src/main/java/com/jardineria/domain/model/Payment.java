package com.jardineria.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    private Integer customerCode;
    private String paymentMethod;
    private String transactionId;
    private LocalDate paymentDate;
    private BigDecimal amount;

    public Payment(Integer customerCode, String paymentMethod, String transactionId, LocalDate paymentDate,
            BigDecimal amount) {
        this.customerCode = customerCode;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public Integer getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(Integer customerCode) {
        this.customerCode = customerCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    
    
}
