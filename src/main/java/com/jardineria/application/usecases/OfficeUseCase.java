package com.jardineria.application.usecases;

import com.jardineria.application.ports.in.OfficeServicePort;
import com.jardineria.domain.model.Office;
import com.jardineria.domain.ports.out.OfficeRepository;

import java.util.List;
import java.util.Optional;

public class OfficeUseCase implements OfficeServicePort {

    private final OfficeRepository officeRepository;

    public OfficeUseCase(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    @Override
    public void save(Office office) {
        officeRepository.save(office);
    }

    @Override
    public List<Office> findAll() {
        return officeRepository.findAll();
    }

    @Override
    public Optional<Office> findById(String officeCode) {
        return officeRepository.findById(officeCode);
    }

    @Override
    public void update(Office office) {
        officeRepository.update(office);
    }

    @Override
    public void deleteById(String officeCode) {
        officeRepository.deleteById(officeCode);
    }
}
