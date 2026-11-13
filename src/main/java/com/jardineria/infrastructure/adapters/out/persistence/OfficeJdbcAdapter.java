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
        String sql = "INSERT INTO office (office_code, city, country, region, postal_code, phone, address_line1, address_line2) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error saving office: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Office> findAll() {
        List<Office> offices = new ArrayList<>();
        String sql = "SELECT office_code, city, country, region, postal_code, phone, address_line1, address_line2 FROM office";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return offices;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    offices.add(mapResultSetToOffice(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving offices: " + e.getMessage());
            e.printStackTrace();
        }

        return offices;
    }

    @Override
    public Optional<Office> findById(String officeCode) {
        String sql = "SELECT office_code, city, country, region, postal_code, phone, address_line1, address_line2 " +
                     "FROM office WHERE office_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
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
            System.err.println("Error finding office by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Office office) {
        String sql = "UPDATE office SET city = ?, country = ?, region = ?, postal_code = ?, phone = ?, " +
                     "address_line1 = ?, address_line2 = ? WHERE office_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error updating office: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(String officeCode) {
        String sql = "DELETE FROM office WHERE office_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, officeCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting office by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Office mapResultSetToOffice(ResultSet resultSet) throws SQLException {
        return new Office(
            resultSet.getString("office_code"),
            resultSet.getString("city"),
            resultSet.getString("country"),
            resultSet.getString("region"),
            resultSet.getString("postal_code"),
            resultSet.getString("phone"),
            resultSet.getString("address_line1"),
            resultSet.getString("address_line2")
        );
    }
}
