package com.jardineria.domain.model;

public class Employee {
    private Integer employeeCode;
    private String firstName;
    private String lastName1;
    private String lastName2;
    private String extension;
    private String email;
    private String officeCode;
    private Integer reportsTo;
    private String jobTitle;


    public Employee(Integer employeeCode, String firstName, String lastName1, String lastName2, String extension,
            String email, String officeCode, Integer reportsTo, String jobTitle) {
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName1 = lastName1;
        this.lastName2 = lastName2;
        this.extension = extension;
        this.email = email;
        this.officeCode = officeCode;
        this.reportsTo = reportsTo;
        this.jobTitle = jobTitle;
    }


    public Integer getEmployeeCode() {
        return employeeCode;
    }


    public void setEmployeeCode(Integer employeeCode) {
        this.employeeCode = employeeCode;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName1() {
        return lastName1;
    }


    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }


    public String getLastName2() {
        return lastName2;
    }


    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }


    public String getExtension() {
        return extension;
    }


    public void setExtension(String extension) {
        this.extension = extension;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getOfficeCode() {
        return officeCode;
    }


    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }


    public Integer getReportsTo() {
        return reportsTo;
    }


    public void setReportsTo(Integer reportsTo) {
        this.reportsTo = reportsTo;
    }


    public String getJobTitle() {
        return jobTitle;
    }


    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    
    
}
