package com.jardineria.domain.model;

import java.math.BigDecimal;

public class Customer {
    private Integer customerCode;
    private String customerName;
    private String contactName;
    private String contactLastName;
    private String phone;
    private String fax;
    private String addressLine1;
    private String city;
    private String country;
    private Integer salesRepEmployeeCode;
    private BigDecimal creditLimit;

    public Customer(Integer customerCode, String customerName, String contactName, String contactLastName, String phone,
            String fax, String addressLine1, String city, String country, Integer salesRepEmployeeCode,
            BigDecimal creditLimit) {
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.contactName = contactName;
        this.contactLastName = contactLastName;
        this.phone = phone;
        this.fax = fax;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.country = country;
        this.salesRepEmployeeCode = salesRepEmployeeCode;
        this.creditLimit = creditLimit;
    }

    public Integer getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(Integer customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getSalesRepEmployeeCode() {
        return salesRepEmployeeCode;
    }

    public void setSalesRepEmployeeCode(Integer salesRepEmployeeCode) {
        this.salesRepEmployeeCode = salesRepEmployeeCode;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    

    
}
