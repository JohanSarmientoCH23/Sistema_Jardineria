package com.jardineria.infrastructure.adapters.out.persistence;

import com.jardineria.domain.model.Payment;
import com.jardineria.domain.model.PaymentId;
import com.jardineria.domain.ports.out.PaymentRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentJdbcAdapter implements PaymentRepository {

    @Override
    public void save(Payment payment) {
        String sql = "INSERT INTO pago (codigo_cliente, forma_pago, id_transaccion, fecha_pago, total) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, payment.getCustomerCode());
                statement.setString(2, payment.getPaymentMethod());
                statement.setString(3, payment.getTransactionId());
                statement.setDate(4, payment.getPaymentDate() != null ? Date.valueOf(payment.getPaymentDate()) : null);
                statement.setBigDecimal(5, payment.getAmount());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT codigo_cliente, forma_pago, id_transaccion, fecha_pago, total FROM pago";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    payments.add(mapResultSetToPayment(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return payments;
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        if (id == null) return Optional.empty();
        return findById(id.customerCode(), id.transactionId());
    }

    @Override
    public Optional<Payment> findById(Integer customerCode, String transactionId) {
        String sql = "SELECT codigo_cliente, forma_pago, id_transaccion, fecha_pago, total " +
                     "FROM pago WHERE codigo_cliente = ? AND id_transaccion = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerCode);
                statement.setString(2, transactionId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(mapResultSetToPayment(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Payment payment) {
        String sql = "UPDATE pago SET forma_pago = ?, fecha_pago = ?, total = ? " +
                     "WHERE codigo_cliente = ? AND id_transaccion = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, payment.getPaymentMethod());
                statement.setDate(2, payment.getPaymentDate() != null ? Date.valueOf(payment.getPaymentDate()) : null);
                statement.setBigDecimal(3, payment.getAmount());
                statement.setInt(4, payment.getCustomerCode());
                statement.setString(5, payment.getTransactionId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(PaymentId id) {
        if (id != null) {
            deleteById(id.customerCode(), id.transactionId());
        }
    }

    @Override
    public void deleteById(Integer customerCode, String transactionId) {
        String sql = "DELETE FROM pago WHERE codigo_cliente = ? AND id_transaccion = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerCode);
                statement.setString(2, transactionId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> findByCustomerCode(Integer customerCode) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT codigo_cliente, forma_pago, id_transaccion, fecha_pago, total FROM pago WHERE codigo_cliente = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                throw new RuntimeException("Error en base de datos: No se pudo conectar a MySQL.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerCode);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        payments.add(mapResultSetToPayment(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        }

        return payments;
    }

    private Payment mapResultSetToPayment(ResultSet resultSet) throws SQLException {
        Date pDate = resultSet.getDate("fecha_pago");
        LocalDate paymentDate = pDate != null ? pDate.toLocalDate() : null;

        return new Payment(
            resultSet.getInt("codigo_cliente"),
            resultSet.getString("forma_pago"),
            resultSet.getString("id_transaccion"),
            paymentDate,
            resultSet.getBigDecimal("total")
        );
    }
}
