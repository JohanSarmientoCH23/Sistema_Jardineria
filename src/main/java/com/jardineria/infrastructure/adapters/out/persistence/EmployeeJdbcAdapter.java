package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.Employee;
import com.jardineria.domain.ports.out.EmployeeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeJdbcAdapter implements EmployeeRepository {

    @Override
    public void save(Employee employee) {
        String sql = "INSERT INTO empleado (codigo_empleado, nombre, apellido1, apellido2, " +
                     "extension, email, codigo_oficina, codigo_jefe, puesto) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, employee.getEmployeeCode());
                statement.setString(2, employee.getFirstName());
                statement.setString(3, employee.getLastName1());
                statement.setString(4, employee.getLastName2());
                statement.setString(5, employee.getExtension());
                statement.setString(6, employee.getEmail());
                statement.setString(7, employee.getOfficeCode());

                if (employee.getReportsTo() != null) {
                    statement.setInt(8, employee.getReportsTo());
                } else {
                    statement.setNull(8, Types.INTEGER);
                }

                statement.setString(9, employee.getJobTitle());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT codigo_empleado, nombre, apellido1, apellido2, " +
                     "extension, email, codigo_oficina, codigo_jefe, puesto FROM empleado";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    employees.add(mapResultSetToEmployee(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return employees;
    }

    @Override
    public Optional<Employee> findById(Integer employeeCode) {
        String sql = "SELECT codigo_empleado, nombre, apellido1, apellido2, " +
                     "extension, email, codigo_oficina, codigo_jefe, puesto " +
                     "FROM empleado WHERE codigo_empleado = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, employeeCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToEmployee(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE empleado SET nombre = ?, apellido1 = ?, apellido2 = ?, " +
                     "extension = ?, email = ?, codigo_oficina = ?, codigo_jefe = ?, puesto = ? " +
                     "WHERE codigo_empleado = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, employee.getFirstName());
                statement.setString(2, employee.getLastName1());
                statement.setString(3, employee.getLastName2());
                statement.setString(4, employee.getExtension());
                statement.setString(5, employee.getEmail());
                statement.setString(6, employee.getOfficeCode());

                if (employee.getReportsTo() != null) {
                    statement.setInt(7, employee.getReportsTo());
                } else {
                    statement.setNull(7, Types.INTEGER);
                }

                statement.setString(8, employee.getJobTitle());
                statement.setInt(9, employee.getEmployeeCode());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer employeeCode) {
        String sql = "DELETE FROM empleado WHERE codigo_empleado = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, employeeCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    private Employee mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        int rep = resultSet.getInt("codigo_jefe");
        Integer reportsTo = resultSet.wasNull() ? null : rep;

        return new Employee(
            resultSet.getInt("codigo_empleado"),
            resultSet.getString("nombre"),
            resultSet.getString("apellido1"),
            resultSet.getString("apellido2"),
            resultSet.getString("extension"),
            resultSet.getString("email"),
            resultSet.getString("codigo_oficina"),
            reportsTo,
            resultSet.getString("puesto")
        );
    }
}
