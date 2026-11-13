package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.ProductLine;
import com.jardineria.domain.ports.out.ProductLineRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductLineJdbcAdapter implements ProductLineRepository {

    @Override
    public void save(ProductLine productLine) {
        String sql = "INSERT INTO gama_producto (gama, descripcion_texto, descripcion_html, imagen) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productLine.getProductLine());
                statement.setString(2, productLine.getTextDescription());
                statement.setString(3, productLine.getHtmlDescription());
                statement.setString(4, productLine.getImage());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductLine> findAll() {
        List<ProductLine> productLines = new ArrayList<>();
        String sql = "SELECT gama, descripcion_texto, descripcion_html, imagen FROM gama_producto";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    productLines.add(mapResultSetToProductLine(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return productLines;
    }

    @Override
    public Optional<ProductLine> findById(String productLine) {
        String sql = "SELECT gama, descripcion_texto, descripcion_html, imagen FROM gama_producto WHERE gama = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productLine);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToProductLine(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(ProductLine productLine) {
        String sql = "UPDATE gama_producto SET descripcion_texto = ?, descripcion_html = ?, imagen = ? WHERE gama = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productLine.getTextDescription());
                statement.setString(2, productLine.getHtmlDescription());
                statement.setString(3, productLine.getImage());
                statement.setString(4, productLine.getProductLine());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String productLine) {
        String sql = "DELETE FROM gama_producto WHERE gama = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productLine);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    private ProductLine mapResultSetToProductLine(ResultSet resultSet) throws SQLException {
        return new ProductLine(
            resultSet.getString("gama"),
            resultSet.getString("descripcion_texto"),
            resultSet.getString("descripcion_html"),
            resultSet.getString("imagen")
        );
    }
}
