package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.Office;
import com.jardineria.domain.ports.out.OfficeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OfficeJdbcAdapter implements OfficeRepository {

    @Override
    public void save(Office office) {
        String sql = "INSERT INTO oficina (codigo_oficina, ciudad, pais, region, codigo_postal, telefono, linea_direccion1, linea_direccion2) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, office.getOfficeCode());
                statement.setString(2, office.getCity());
                statement.setString(3, office.getCountry());
                statement.setString(4, office.getRegion());
                statement.setString(5, office.getPostalCode());
                statement.setString(6, office.getPhone());
                statement.setString(7, office.getAddressLine1());
                statement.setString(8, office.getAddressLine2());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Office> findAll() {
        List<Office> offices = new ArrayList<>();
        String sql = "SELECT codigo_oficina, ciudad, pais, region, codigo_postal, telefono, linea_direccion1, linea_direccion2 FROM oficina";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    offices.add(mapResultSetToOffice(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return offices;
    }

    @Override
    public Optional<Office> findById(String officeCode) {
        String sql = "SELECT codigo_oficina, ciudad, pais, region, codigo_postal, telefono, linea_direccion1, linea_direccion2 " +
                     "FROM oficina WHERE codigo_oficina = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, officeCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToOffice(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Office office) {
        String sql = "UPDATE oficina SET ciudad = ?, pais = ?, region = ?, codigo_postal = ?, telefono = ?, " +
                     "linea_direccion1 = ?, linea_direccion2 = ? WHERE codigo_oficina = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, office.getCity());
                statement.setString(2, office.getCountry());
                statement.setString(3, office.getRegion());
                statement.setString(4, office.getPostalCode());
                statement.setString(5, office.getPhone());
                statement.setString(6, office.getAddressLine1());
                statement.setString(7, office.getAddressLine2());
                statement.setString(8, office.getOfficeCode());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String officeCode) {
        String sql = "DELETE FROM oficina WHERE codigo_oficina = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, officeCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    private Office mapResultSetToOffice(ResultSet resultSet) throws SQLException {
        return new Office(
            resultSet.getString("codigo_oficina"),
            resultSet.getString("ciudad"),
            resultSet.getString("pais"),
            resultSet.getString("region"),
            resultSet.getString("codigo_postal"),
            resultSet.getString("telefono"),
            resultSet.getString("linea_direccion1"),
            resultSet.getString("linea_direccion2")
        );
    }
}
