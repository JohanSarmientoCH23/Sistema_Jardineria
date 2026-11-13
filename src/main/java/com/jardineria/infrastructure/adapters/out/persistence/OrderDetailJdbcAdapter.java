package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.OrderDetail;
import com.jardineria.domain.model.OrderDetailId;
import com.jardineria.domain.ports.out.OrderDetailRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDetailJdbcAdapter implements OrderDetailRepository {

    @Override
    public void save(OrderDetail orderDetail) {
        String sql = "INSERT INTO order_detail (order_code, product_code, quantity_ordered, price_each, order_line_number) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderDetail.getOrderCode());
                statement.setString(2, orderDetail.getProductCode());
                statement.setInt(3, orderDetail.getQuantityOrdered());
                statement.setBigDecimal(4, orderDetail.getPriceEach());

                if (orderDetail.getOrderLineNumber() != null) {
                    statement.setShort(5, orderDetail.getOrderLineNumber());
                } else {
                    statement.setNull(5, Types.SMALLINT);
                }

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error saving order detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<OrderDetail> findAll() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT order_code, product_code, quantity_ordered, price_each, order_line_number FROM order_detail";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return orderDetails;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    orderDetails.add(mapResultSetToOrderDetail(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving order details: " + e.getMessage());
            e.printStackTrace();
        }

        return orderDetails;
    }

    @Override
    public Optional<OrderDetail> findById(OrderDetailId id) {
        if (id == null) {
            return Optional.empty();
        }
        return findById(id.orderCode(), id.productCode());
    }

    @Override
    public Optional<OrderDetail> findById(Integer orderCode, String productCode) {
        String sql = "SELECT order_code, product_code, quantity_ordered, price_each, order_line_number " +
                     "FROM order_detail WHERE order_code = ? AND product_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderCode);
                statement.setString(2, productCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToOrderDetail(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order detail by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(OrderDetail orderDetail) {
        String sql = "UPDATE order_detail SET quantity_ordered = ?, price_each = ?, order_line_number = ? " +
                     "WHERE order_code = ? AND product_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderDetail.getQuantityOrdered());
                statement.setBigDecimal(2, orderDetail.getPriceEach());

                if (orderDetail.getOrderLineNumber() != null) {
                    statement.setShort(3, orderDetail.getOrderLineNumber());
                } else {
                    statement.setNull(3, Types.SMALLINT);
                }

                statement.setInt(4, orderDetail.getOrderCode());
                statement.setString(5, orderDetail.getProductCode());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating order detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(OrderDetailId id) {
        if (id != null) {
            deleteById(id.orderCode(), id.productCode());
        }
    }

    @Override
    public void deleteById(Integer orderCode, String productCode) {
        String sql = "DELETE FROM order_detail WHERE order_code = ? AND product_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderCode);
                statement.setString(2, productCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting order detail by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<OrderDetail> findByOrderCode(Integer orderCode) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT order_code, product_code, quantity_ordered, price_each, order_line_number " +
                     "FROM order_detail WHERE order_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return orderDetails;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        orderDetails.add(mapResultSetToOrderDetail(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order details by order code: " + e.getMessage());
            e.printStackTrace();
        }

        return orderDetails;
    }

    private OrderDetail mapResultSetToOrderDetail(ResultSet resultSet) throws SQLException {
        short lineNum = resultSet.getShort("order_line_number");
        Short orderLineNumber = resultSet.wasNull() ? null : lineNum;

        return new OrderDetail(
            resultSet.getInt("order_code"),
            resultSet.getString("product_code"),
            resultSet.getInt("quantity_ordered"),
            resultSet.getBigDecimal("price_each"),
            orderLineNumber
        );
    }
}
