package com.jardineria.application.ports.in;

import com.jardineria.domain.model.ProductLine;

import java.util.List;
import java.util.Optional;

public interface ProductLineServicePort {
    void save(ProductLine productLine);
    List<ProductLine> findAll();
    Optional<ProductLine> findById(String productLine);
    void update(ProductLine productLine);
    void deleteById(String productLine);
}
