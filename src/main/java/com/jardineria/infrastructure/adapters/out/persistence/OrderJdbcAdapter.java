package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.Order;
import com.jardineria.domain.ports.out.OrderRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderJdbcAdapter implements OrderRepository {

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO `order` (order_code, order_date, required_date, shipped_date, status, comments, customer_code) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, order.getOrderCode());
                statement.setDate(2, order.getOrderDate() != null ? Date.valueOf(order.getOrderDate()) : null);
                statement.setDate(3, order.getRequiredDate() != null ? Date.valueOf(order.getRequiredDate()) : null);
                statement.setDate(4, order.getShippedDate() != null ? Date.valueOf(order.getShippedDate()) : null);
                statement.setString(5, order.getStatus());
                statement.setString(6, order.getComments());
                statement.setInt(7, order.getCustomerCode());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT order_code, order_date, required_date, shipped_date, status, comments, customer_code FROM `order`";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return orders;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    orders.add(mapResultSetToOrder(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving orders: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }

    @Override
    public Optional<Order> findById(Integer orderCode) {
        String sql = "SELECT order_code, order_date, required_date, shipped_date, status, comments, customer_code " +
                     "FROM `order` WHERE order_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToOrder(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE `order` SET order_date = ?, required_date = ?, shipped_date = ?, " +
                     "status = ?, comments = ?, customer_code = ? WHERE order_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDate(1, order.getOrderDate() != null ? Date.valueOf(order.getOrderDate()) : null);
                statement.setDate(2, order.getRequiredDate() != null ? Date.valueOf(order.getRequiredDate()) : null);
                statement.setDate(3, order.getShippedDate() != null ? Date.valueOf(order.getShippedDate()) : null);
                statement.setString(4, order.getStatus());
                statement.setString(5, order.getComments());
                statement.setInt(6, order.getCustomerCode());
                statement.setInt(7, order.getOrderCode());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Integer orderCode) {
        String sql = "DELETE FROM `order` WHERE order_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting order by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Order mapResultSetToOrder(ResultSet resultSet) throws SQLException {
        Date oDate = resultSet.getDate("order_date");
        LocalDate orderDate = oDate != null ? oDate.toLocalDate() : null;

        Date rDate = resultSet.getDate("required_date");
        LocalDate requiredDate = rDate != null ? rDate.toLocalDate() : null;

        Date sDate = resultSet.getDate("shipped_date");
        LocalDate shippedDate = sDate != null ? sDate.toLocalDate() : null;

        return new Order(
            resultSet.getInt("order_code"),
            orderDate,
            requiredDate,
            shippedDate,
            resultSet.getString("status"),
            resultSet.getString("comments"),
            resultSet.getInt("customer_code")
        );
    }
}
