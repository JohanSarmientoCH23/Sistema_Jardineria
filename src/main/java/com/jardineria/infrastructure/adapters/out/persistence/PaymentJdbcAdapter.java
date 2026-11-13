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
        String sql = "INSERT INTO payment (customer_code, payment_method, transaction_id, payment_date, amount) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error saving payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT customer_code, payment_method, transaction_id, payment_date, amount FROM payment";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return payments;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    payments.add(mapResultSetToPayment(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
            e.printStackTrace();
        }

        return payments;
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        if (id == null) {
            return Optional.empty();
        }
        return findById(id.customerCode(), id.transactionId());
    }

    @Override
    public Optional<Payment> findById(Integer customerCode, String transactionId) {
        String sql = "SELECT customer_code, payment_method, transaction_id, payment_date, amount " +
                     "FROM payment WHERE customer_code = ? AND transaction_id = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return Optional.empty();
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
            System.err.println("Error finding payment by id: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Payment payment) {
        String sql = "UPDATE payment SET payment_method = ?, payment_date = ?, amount = ? " +
                     "WHERE customer_code = ? AND transaction_id = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
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
            System.err.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
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
        String sql = "DELETE FROM payment WHERE customer_code = ? AND transaction_id = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerCode);
                statement.setString(2, transactionId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting payment by id: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Payment> findByCustomerCode(Integer customerCode) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT customer_code, payment_method, transaction_id, payment_date, amount " +
                     "FROM payment WHERE customer_code = ?";

        try (Connection connection = ConexionMySQL.conectar()) {
            if (connection == null) {
                System.err.println("Could not establish connection to MySQL database.");
                return payments;
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
            System.err.println("Error finding payments by customer code: " + e.getMessage());
            e.printStackTrace();
        }

        return payments;
    }

    private Payment mapResultSetToPayment(ResultSet resultSet) throws SQLException {
        Date pDate = resultSet.getDate("payment_date");
        LocalDate paymentDate = pDate != null ? pDate.toLocalDate() : null;

        return new Payment(
            resultSet.getInt("customer_code"),
            resultSet.getString("payment_method"),
            resultSet.getString("transaction_id"),
            paymentDate,
            resultSet.getBigDecimal("amount")
        );
    }
}
