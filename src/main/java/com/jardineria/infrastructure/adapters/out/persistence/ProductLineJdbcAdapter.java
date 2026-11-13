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
        String sql = "INSERT INTO product_line (product_line, text_description, html_description, image) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productLine.getProductLine());
                statement.setString(2, productLine.getTextDescription());
                statement.setString(3, productLine.getHtmlDescription());
                statement.setString(4, productLine.getImage());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error saving product line: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<ProductLine> findAll() {
        List<ProductLine> productLines = new ArrayList<>();
        String sql = "SELECT product_line, text_description, html_description, image FROM product_line";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return productLines;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    productLines.add(mapResultSetToProductLine(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving product lines: " + e.getMessage());
            e.printStackTrace();
        }

        return productLines;
    }

    @Override
    public Optional<ProductLine> findById(String productLine) {
        String sql = "SELECT product_line, text_description, html_description, image " +
                     "FROM product_line WHERE product_line = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
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
            System.err.println("Error finding product line by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(ProductLine productLine) {
        String sql = "UPDATE product_line SET text_description = ?, html_description = ?, image = ? " +
                     "WHERE product_line = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productLine.getTextDescription());
                statement.setString(2, productLine.getHtmlDescription());
                statement.setString(3, productLine.getImage());
                statement.setString(4, productLine.getProductLine());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating product line: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(String productLine) {
        String sql = "DELETE FROM product_line WHERE product_line = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productLine);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting product line by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ProductLine mapResultSetToProductLine(ResultSet resultSet) throws SQLException {
        return new ProductLine(
            resultSet.getString("product_line"),
            resultSet.getString("text_description"),
            resultSet.getString("html_description"),
            resultSet.getString("image")
        );
    }
}
