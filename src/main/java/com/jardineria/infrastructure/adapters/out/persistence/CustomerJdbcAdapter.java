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

    @Override
    public void save(Customer customer) {
        String sql = "INSERT INTO customer (customer_code, customer_name, contact_name, contact_last_name, " +
                     "phone, fax, address_line1, city, country, sales_rep_employee_code, credit_limit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error saving customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_code, customer_name, contact_name, contact_last_name, " +
                     "phone, fax, address_line1, city, country, sales_rep_employee_code, credit_limit " +
                     "FROM customer";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return customers;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    customers.add(mapResultSetToCustomer(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    @Override
    public Optional<Customer> findById(Integer customerCode) {
        String sql = "SELECT customer_code, customer_name, contact_name, contact_last_name, " +
                     "phone, fax, address_line1, city, country, sales_rep_employee_code, credit_limit " +
                     "FROM customer WHERE customer_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
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
            System.err.println("Error finding customer by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Customer customer) {
        String sql = "UPDATE customer SET customer_name = ?, contact_name = ?, contact_last_name = ?, " +
                     "phone = ?, fax = ?, address_line1 = ?, city = ?, country = ?, " +
                     "sales_rep_employee_code = ?, credit_limit = ? WHERE customer_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Integer customerCode) {
        String sql = "DELETE FROM customer WHERE customer_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting customer by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Customer mapResultSetToCustomer(ResultSet resultSet) throws SQLException {
        int salesRep = resultSet.getInt("sales_rep_employee_code");
        Integer salesRepEmployeeCode = resultSet.wasNull() ? null : salesRep;

        return new Customer(
            resultSet.getInt("customer_code"),
            resultSet.getString("customer_name"),
            resultSet.getString("contact_name"),
            resultSet.getString("contact_last_name"),
            resultSet.getString("phone"),
            resultSet.getString("fax"),
            resultSet.getString("address_line1"),
            resultSet.getString("city"),
            resultSet.getString("country"),
            salesRepEmployeeCode,
            resultSet.getBigDecimal("credit_limit")
        );
    }
}
