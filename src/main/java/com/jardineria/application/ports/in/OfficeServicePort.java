package com.jardineria.application.ports.in;

import com.jardineria.domain.model.Office;

import java.util.List;
import java.util.Optional;

public interface OfficeServicePort {
    void save(Office office);
    List<Office> findAll();
    Optional<Office> findById(String officeCode);
    void update(Office office);
    void deleteById(String officeCode);
}
