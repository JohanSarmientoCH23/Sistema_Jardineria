package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.Product;
import com.jardineria.domain.ports.out.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductJdbcAdapter implements ProductRepository {

    @Override
    public void save(Product product) {
        String sql = "INSERT INTO producto (codigo_producto, nombre, gama, dimensiones, proveedor, " +
                     "descripcion, cantidad_en_stock, precio_proveedor, precio_venta) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, product.getProductCode());
                statement.setString(2, product.getName());
                statement.setString(3, product.getProductLine());
                statement.setString(4, product.getDimensions());
                statement.setString(5, product.getSupplier());
                statement.setString(6, product.getDescription());

                if (product.getQuantityInStock() != null) {
                    statement.setShort(7, product.getQuantityInStock());
                } else {
                    statement.setNull(7, Types.SMALLINT);
                }

                if (product.getBuyPrice() != null) {
                    statement.setBigDecimal(8, product.getBuyPrice());
                } else {
                    statement.setNull(8, Types.DECIMAL);
                }

                if (product.getMsrp() != null) {
                    statement.setBigDecimal(9, product.getMsrp());
                } else {
                    statement.setNull(9, Types.DECIMAL);
                }

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT codigo_producto, nombre, gama, dimensiones, proveedor, " +
                     "descripcion, cantidad_en_stock, precio_proveedor, precio_venta FROM producto";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(mapResultSetToProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return products;
    }

    @Override
    public Optional<Product> findById(String productCode) {
        String sql = "SELECT codigo_producto, nombre, gama, dimensiones, proveedor, " +
                     "descripcion, cantidad_en_stock, precio_proveedor, precio_venta " +
                     "FROM producto WHERE codigo_producto = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToProduct(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE producto SET nombre = ?, gama = ?, dimensiones = ?, proveedor = ?, " +
                     "descripcion = ?, cantidad_en_stock = ?, precio_proveedor = ?, precio_venta = ? " +
                     "WHERE codigo_producto = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, product.getName());
                statement.setString(2, product.getProductLine());
                statement.setString(3, product.getDimensions());
                statement.setString(4, product.getSupplier());
                statement.setString(5, product.getDescription());

                if (product.getQuantityInStock() != null) {
                    statement.setShort(6, product.getQuantityInStock());
                } else {
                    statement.setNull(6, Types.SMALLINT);
                }

                if (product.getBuyPrice() != null) {
                    statement.setBigDecimal(7, product.getBuyPrice());
                } else {
                    statement.setNull(7, Types.DECIMAL);
                }

                if (product.getMsrp() != null) {
                    statement.setBigDecimal(8, product.getMsrp());
                } else {
                    statement.setNull(8, Types.DECIMAL);
                }

                statement.setString(9, product.getProductCode());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String productCode) {
        String sql = "DELETE FROM producto WHERE codigo_producto = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        short qStock = resultSet.getShort("cantidad_en_stock");
        Short quantityInStock = resultSet.wasNull() ? null : qStock;

        return new Product(
            resultSet.getString("codigo_producto"),
            resultSet.getString("nombre"),
            resultSet.getString("gama"),
            resultSet.getString("dimensiones"),
            resultSet.getString("proveedor"),
            resultSet.getString("descripcion"),
            quantityInStock,
            resultSet.getBigDecimal("precio_proveedor"),
            resultSet.getBigDecimal("precio_venta")
        );
    }
}
