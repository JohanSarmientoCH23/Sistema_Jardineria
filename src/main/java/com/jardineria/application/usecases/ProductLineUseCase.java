package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.ProductLineServicePort;
import com.jardineria.domain.model.ProductLine;
import com.jardineria.domain.ports.out.ProductLineRepository;

import java.util.List;
import java.util.Optional;

public class ProductLineUseCase implements ProductLineServicePort {

    private final ProductLineRepository productLineRepository;

    public ProductLineUseCase(ProductLineRepository productLineRepository) {
        this.productLineRepository = productLineRepository;
    }

    @Override
    public void save(ProductLine productLine) {
        productLineRepository.save(productLine);
    }

    @Override
    public List<ProductLine> findAll() {
        return productLineRepository.findAll();
    }

    @Override
    public Optional<ProductLine> findById(String productLine) {
        return productLineRepository.findById(productLine);
    }

    @Override
    public void update(ProductLine productLine) {
        productLineRepository.update(productLine);
    }

    @Override
    public void deleteById(String productLine) {
        productLineRepository.deleteById(productLine);
    }
}
