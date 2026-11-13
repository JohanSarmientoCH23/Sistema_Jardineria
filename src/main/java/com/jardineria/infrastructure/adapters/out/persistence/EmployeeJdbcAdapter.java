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
        String sql = "INSERT INTO employee (employee_code, first_name, last_name1, last_name2, " +
                     "extension, email, office_code, reports_to, job_title) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error saving employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT employee_code, first_name, last_name1, last_name2, " +
                     "extension, email, office_code, reports_to, job_title FROM employee";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return employees;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    employees.add(mapResultSetToEmployee(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving employees: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public Optional<Employee> findById(Integer employeeCode) {
        String sql = "SELECT employee_code, first_name, last_name1, last_name2, " +
                     "extension, email, office_code, reports_to, job_title " +
                     "FROM employee WHERE employee_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
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
            System.err.println("Error finding employee by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE employee SET first_name = ?, last_name1 = ?, last_name2 = ?, " +
                     "extension = ?, email = ?, office_code = ?, reports_to = ?, job_title = ? " +
                     "WHERE employee_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Integer employeeCode) {
        String sql = "DELETE FROM employee WHERE employee_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, employeeCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting employee by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Employee mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        int rep = resultSet.getInt("reports_to");
        Integer reportsTo = resultSet.wasNull() ? null : rep;

        return new Employee(
            resultSet.getInt("employee_code"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name1"),
            resultSet.getString("last_name2"),
            resultSet.getString("extension"),
            resultSet.getString("email"),
            resultSet.getString("office_code"),
            reportsTo,
            resultSet.getString("job_title")
        );
    }
}
