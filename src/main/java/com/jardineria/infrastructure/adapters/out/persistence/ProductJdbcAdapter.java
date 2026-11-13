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
        String sql = "INSERT INTO product (product_code, name, product_line, dimensions, supplier, " +
                     "description, quantity_in_stock, buy_price, msrp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error saving product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_code, name, product_line, dimensions, supplier, " +
                     "description, quantity_in_stock, buy_price, msrp FROM product";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return products;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(mapResultSetToProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    @Override
    public Optional<Product> findById(String productCode) {
        String sql = "SELECT product_code, name, product_line, dimensions, supplier, " +
                     "description, quantity_in_stock, buy_price, msrp FROM product WHERE product_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
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
            System.err.println("Error finding product by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE product SET name = ?, product_line = ?, dimensions = ?, supplier = ?, " +
                     "description = ?, quantity_in_stock = ?, buy_price = ?, msrp = ? WHERE product_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(String productCode) {
        String sql = "DELETE FROM product WHERE product_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting product by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        short qStock = resultSet.getShort("quantity_in_stock");
        Short quantityInStock = resultSet.wasNull() ? null : qStock;

        return new Product(
            resultSet.getString("product_code"),
            resultSet.getString("name"),
            resultSet.getString("product_line"),
            resultSet.getString("dimensions"),
            resultSet.getString("supplier"),
            resultSet.getString("description"),
            quantityInStock,
            resultSet.getBigDecimal("buy_price"),
            resultSet.getBigDecimal("msrp")
        );
    }
}
