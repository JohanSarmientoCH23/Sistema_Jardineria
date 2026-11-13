package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.ProductServicePort;
import com.jardineria.domain.model.Product;
import com.jardineria.domain.ports.out.ProductRepository;

import java.util.List;
import java.util.Optional;

public class ProductUseCase implements ProductServicePort {

    private final ProductRepository productRepository;

    public ProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(String productCode) {
        return productRepository.findById(productCode);
    }

    @Override
    public void update(Product product) {
        productRepository.update(product);
    }

    @Override
    public void deleteById(String productCode) {
        productRepository.deleteById(productCode);
    }
}
