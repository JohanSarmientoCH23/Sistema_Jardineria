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
        String sql = "INSERT INTO detalle_pedido (codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
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
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrderDetail> findAll() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea FROM detalle_pedido";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    orderDetails.add(mapResultSetToOrderDetail(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return orderDetails;
    }

    @Override
    public Optional<OrderDetail> findById(OrderDetailId id) {
        if (id == null) return Optional.empty();
        return findById(id.orderCode(), id.productCode());
    }

    @Override
    public Optional<OrderDetail> findById(Integer orderCode, String productCode) {
        String sql = "SELECT codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea " +
                     "FROM detalle_pedido WHERE codigo_pedido = ? AND codigo_producto = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
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
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(OrderDetail orderDetail) {
        String sql = "UPDATE detalle_pedido SET cantidad = ?, precio_unidad = ?, numero_linea = ? " +
                     "WHERE codigo_pedido = ? AND codigo_producto = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
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
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
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
        String sql = "DELETE FROM detalle_pedido WHERE codigo_pedido = ? AND codigo_producto = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, orderCode);
                statement.setString(2, productCode);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrderDetail> findByOrderCode(Integer orderCode) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea " +
                     "FROM detalle_pedido WHERE codigo_pedido = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
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
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return orderDetails;
    }

    private OrderDetail mapResultSetToOrderDetail(ResultSet resultSet) throws SQLException {
        short lineNum = resultSet.getShort("numero_linea");
        Short orderLineNumber = resultSet.wasNull() ? null : lineNum;

        return new OrderDetail(
            resultSet.getInt("codigo_pedido"),
            resultSet.getString("codigo_producto"),
            resultSet.getInt("cantidad"),
            resultSet.getBigDecimal("precio_unidad"),
            orderLineNumber
        );
    }
}
