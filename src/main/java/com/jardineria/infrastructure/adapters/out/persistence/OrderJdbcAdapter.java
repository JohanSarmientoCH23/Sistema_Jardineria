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
        String sql = "INSERT INTO pedido (codigo_pedido, fecha_pedido, fecha_esperada, fecha_entrega, estado, comentarios, codigo_cliente) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
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
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT codigo_pedido, fecha_pedido, fecha_esperada, fecha_entrega, estado, comentarios, codigo_cliente FROM pedido";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    orders.add(mapResultSetToOrder(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return orders;
    }

    @Override
    public Optional<Order> findById(Integer orderCode) {
        String sql = "SELECT codigo_pedido, fecha_pedido, fecha_esperada, fecha_entrega, estado, comentarios, codigo_cliente " +
                     "FROM pedido WHERE codigo_pedido = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
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
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE pedido SET fecha_pedido = ?, fecha_esperada = ?, fecha_entrega = ?, " +
                     "estado = ?, comentarios = ?, codigo_cliente = ? WHERE codigo_pedido = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
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
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer orderCode) {
        String sql = "DELETE FROM pedido WHERE codigo_pedido = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    private Order mapResultSetToOrder(ResultSet resultSet) throws SQLException {
        Date oDate = resultSet.getDate("fecha_pedido");
        LocalDate orderDate = oDate != null ? oDate.toLocalDate() : null;

        Date rDate = resultSet.getDate("fecha_esperada");
        LocalDate requiredDate = rDate != null ? rDate.toLocalDate() : null;

        Date sDate = resultSet.getDate("fecha_entrega");
        LocalDate shippedDate = sDate != null ? sDate.toLocalDate() : null;

        return new Order(
            resultSet.getInt("codigo_pedido"),
            orderDate,
            requiredDate,
            shippedDate,
            resultSet.getString("estado"),
            resultSet.getString("comentarios"),
            resultSet.getInt("codigo_cliente")
        );
    }
}
