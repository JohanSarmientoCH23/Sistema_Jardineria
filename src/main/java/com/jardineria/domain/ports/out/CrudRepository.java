package com.jardineria.domain.ports.out;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    void save(T entity);
    List<T> findAll();
    Optional<T> findById(ID id);
    void update(T entity);
    void deleteById(ID id);
}
