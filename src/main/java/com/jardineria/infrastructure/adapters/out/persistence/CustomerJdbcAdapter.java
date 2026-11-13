package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.Customer;
import com.jardineria.domain.ports.out.CustomerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerJdbcAdapter implements CustomerRepository {

    /**
     * Obtiene el siguiente código secuencial disponible para cliente.
     * Si la tabla está vacía, devuelve 1; de lo contrario, devuelve MAX + 1.
     */
    private int getNextCustomerCode() {
        String sql = "SELECT MAX(codigo_cliente) FROM cliente";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo establecer la conexión con MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int maxCode = resultSet.getInt(1);
                    return resultSet.wasNull() || maxCode == 0 ? 1 : maxCode + 1;
                }
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos al generar código de cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(Customer customer) {
        // Asignar automáticamente el siguiente código secuencial
        int nextCode = getNextCustomerCode();
        customer.setCustomerCode(nextCode);

        String sql = "INSERT INTO cliente (codigo_cliente, nombre_cliente, nombre_contacto, apellido_contacto, " +
                     "telefono, fax, linea_direccion1, ciudad, pais, codigo_empleado_rep_ventas, limite_credito) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo establecer la conexión con MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customer.getCustomerCode());
                statement.setString(2, customer.getCustomerName());
                statement.setString(3, customer.getContactName());
                statement.setString(4, customer.getContactLastName());
                statement.setString(5, customer.getPhone());
                statement.setString(6, customer.getFax());
                statement.setString(7, customer.getAddressLine1());
                statement.setString(8, customer.getCity());
                statement.setString(9, customer.getCountry());

                if (customer.getSalesRepEmployeeCode() != null) {
                    statement.setInt(10, customer.getSalesRepEmployeeCode());
                } else {
                    statement.setNull(10, Types.INTEGER);
                }

                if (customer.getCreditLimit() != null) {
                    statement.setBigDecimal(11, customer.getCreditLimit());
                } else {
                    statement.setNull(11, Types.DECIMAL);
                }

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT codigo_cliente, nombre_cliente, nombre_contacto, apellido_contacto, " +
                     "telefono, fax, linea_direccion1, ciudad, pais, codigo_empleado_rep_ventas, limite_credito " +
                     "FROM cliente";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo establecer la conexión con MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    customers.add(mapResultSetToCustomer(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return customers;
    }

    @Override
    public Optional<Customer> findById(Integer customerCode) {
        String sql = "SELECT codigo_cliente, nombre_cliente, nombre_contacto, apellido_contacto, " +
                     "telefono, fax, linea_direccion1, ciudad, pais, codigo_empleado_rep_ventas, limite_credito " +
                     "FROM cliente WHERE codigo_cliente = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo establecer la conexión con MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToCustomer(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Customer customer) {
        String sql = "UPDATE cliente SET nombre_cliente = ?, nombre_contacto = ?, apellido_contacto = ?, " +
                     "telefono = ?, fax = ?, linea_direccion1 = ?, ciudad = ?, pais = ?, " +
                     "codigo_empleado_rep_ventas = ?, limite_credito = ? WHERE codigo_cliente = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo establecer la conexión con MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, customer.getCustomerName());
                statement.setString(2, customer.getContactName());
                statement.setString(3, customer.getContactLastName());
                statement.setString(4, customer.getPhone());
                statement.setString(5, customer.getFax());
                statement.setString(6, customer.getAddressLine1());
                statement.setString(7, customer.getCity());
                statement.setString(8, customer.getCountry());

                if (customer.getSalesRepEmployeeCode() != null) {
                    statement.setInt(9, customer.getSalesRepEmployeeCode());
                } else {
                    statement.setNull(9, Types.INTEGER);
                }

                if (customer.getCreditLimit() != null) {
                    statement.setBigDecimal(10, customer.getCreditLimit());
                } else {
                    statement.setNull(10, Types.DECIMAL);
                }

                statement.setInt(11, customer.getCustomerCode());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer customerCode) {
        String sql = "DELETE FROM cliente WHERE codigo_cliente = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo establecer la conexión con MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet resultSet) throws SQLException {
        int salesRep = resultSet.getInt("codigo_empleado_rep_ventas");
        Integer salesRepEmployeeCode = resultSet.wasNull() ? null : salesRep;

        return new Customer(
            resultSet.getInt("codigo_cliente"),
            resultSet.getString("nombre_cliente"),
            resultSet.getString("nombre_contacto"),
            resultSet.getString("apellido_contacto"),
            resultSet.getString("telefono"),
            resultSet.getString("fax"),
            resultSet.getString("linea_direccion1"),
            resultSet.getString("ciudad"),
            resultSet.getString("pais"),
            salesRepEmployeeCode,
            resultSet.getBigDecimal("limite_credito")
        );
    }
}
