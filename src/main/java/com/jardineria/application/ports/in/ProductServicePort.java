package com.jardineria.application.ports.in;

import com.jardineria.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductServicePort {
    void save(Product product);
    List<Product> findAll();
    Optional<Product> findById(String productCode);
    void update(Product product);
    void deleteById(String productCode);
}
