package com.jardineria.infrastructure.adapters.in.ui.views;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.math.BigDecimal;

public class UIUtils {

    private static final BigDecimal MAX_DECIMAL_15_2 = new BigDecimal("9999999999999.99"); // 13 integer digits, 2 decimals

    /**
     * Cleans currency/number input (removes $, spaces, replaces commas with dots)
     * and validates that it conforms to MySQL DECIMAL(15,2) constraints.
     */
    public static BigDecimal parseAmount(String input, String fieldName, boolean allowZero) {
        if (input == null || input.trim().isEmpty()) {
            if (allowZero) return BigDecimal.ZERO;
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String cleaned = input.replace("$", "").replace(" ", "").replace(",", ".").trim();
        BigDecimal value;
        try {
            value = new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number (e.g. 150.50).");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }

        if (!allowZero && value.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero.");
        }

        if (value.compareTo(MAX_DECIMAL_15_2) > 0) {
            throw new IllegalArgumentException(fieldName + " is out of range. Maximum allowed value is 9,999,999,999,999.99 (up to 13 digits before decimal).");
        }

        return value;
    }

    /**
     * Formats database exceptions into clear, actionable messages for the user.
     */
    public static String getFriendlyErrorMessage(Exception ex, String operation, String entityName) {
        if (ex == null) return "An unexpected error occurred.";
        String msg = ex.getMessage() != null ? ex.getMessage() : ex.toString();

        if (msg.contains("Out of range value for column") || msg.contains("Data truncation: Out of range")) {
            return "Out of Range Error: One of the numerical amounts entered exceeds the database limit.\n" +
                   "Please ensure monetary or numerical values do not exceed 13 integer digits.";
        }

        if (msg.contains("Data too long for column")) {
            return "Text Length Error: One of the entered texts exceeds the maximum character limit for this field in the database.";
        }

        if (msg.contains("a foreign key constraint fails") || msg.contains("foreign key constraint fails")) {
            if (operation.equalsIgnoreCase("delete")) {
                return "Cannot delete this " + entityName + " because other records depend on it.\n" +
                       "(e.g., related orders, line items, payments, or employees). Remove or reassign them first.";
            } else {
                return "Foreign Key Error: The referenced related record (Customer, Office, Product Line, or Order) does not exist in the database.\n" +
                       "Please select or specify an existing reference code.";
            }
        }

        if (msg.contains("Duplicate entry")) {
            return "Duplicate Identifier Error: A " + entityName + " with this primary code already exists.\n" +
                   "Please enter a unique identifier.";
        }

        if (msg.contains("Communications link failure") || msg.contains("No se pudo")) {
            return "Database Connection Error: Could not connect to the MySQL database.\n" +
                   "Please check that Docker and MySQL on localhost:3307 are running.";
        }

        return msg;
    }

    /**
     * Shows a standard error dialog with formatted message.
     */
    public static void showError(Component parent, String title, Exception ex, String operation, String entityName) {
        String friendlyMessage = getFriendlyErrorMessage(ex, operation, entityName);
        JOptionPane.showMessageDialog(parent, friendlyMessage, title, JOptionPane.ERROR_MESSAGE);
    }
}
