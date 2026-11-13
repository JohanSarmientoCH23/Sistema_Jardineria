package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.CustomerUseCase;
import com.jardineria.application.usecases.PaymentUseCase;
import com.jardineria.domain.model.Customer;
import com.jardineria.domain.model.Payment;
import com.jardineria.infrastructure.adapters.out.persistence.CustomerJdbcAdapter;
import com.jardineria.infrastructure.adapters.out.persistence.PaymentJdbcAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PaymentView extends JPanel {

    private final PaymentUseCase paymentUseCase = new PaymentUseCase(new PaymentJdbcAdapter());
    private final CustomerUseCase customerUseCase = new CustomerUseCase(new CustomerJdbcAdapter());
    private DefaultTableModel tableModel;
    private JTable paymentTable;

    public PaymentView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Top Panel: Title and Action Buttons (Add, Edit, Delete, Refresh)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Payments Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        Dimension btnSize = new Dimension(95, 34);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnRefresh.setPreferredSize(btnSize);

        btnAdd.addActionListener(e -> showPaymentDialog(null));
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnRefresh.addActionListener(e -> loadData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: JScrollPane with JTable
        String[] columns = {"Customer Code", "Payment Method", "Transaction ID", "Payment Date", "Total"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.setRowHeight(28);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Payment> payments = paymentUseCase.findAll();
            for (Payment p : payments) {
                tableModel.addRow(new Object[]{
                        p.getCustomerCode(),
                        p.getPaymentMethod(),
                        p.getTransactionId(),
                        p.getPaymentDate() != null ? p.getPaymentDate().toString() : "",
                        p.getAmount() != null ? String.format("$%.2f", p.getAmount()) : "$0.00"
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Payments");
        }
    }

    private void handleEdit() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a payment from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = paymentTable.convertRowIndexToModel(selectedRow);
        Integer customerCode = (Integer) tableModel.getValueAt(modelRow, 0);
        String transactionId = (String) tableModel.getValueAt(modelRow, 2);

        paymentUseCase.findById(customerCode, transactionId).ifPresentOrElse(
                this::showPaymentDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected payment record not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a payment from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = paymentTable.convertRowIndexToModel(selectedRow);
        Integer customerCode = (Integer) tableModel.getValueAt(modelRow, 0);
        String transactionId = (String) tableModel.getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete payment transaction '" + transactionId + "' for Customer #" + customerCode + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                paymentUseCase.deleteById(customerCode, transactionId);
                JOptionPane.showMessageDialog(this,
                        "Payment deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Payment");
            }
        }
    }

    private void showPaymentDialog(Payment paymentToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        PaymentFormDialog dialog = new PaymentFormDialog(owner, paymentToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // Modal Form Dialog for Add / Edit
    private class PaymentFormDialog extends JDialog {
        private final Payment paymentToEdit;
        private boolean saved = false;

        private JComboBox<String> cmbCustomer;
        private JComboBox<String> cmbPaymentMethod;
        private JTextField txtTransactionId;
        private JTextField txtPaymentDate;
        private JTextField txtTotal;

        public PaymentFormDialog(Frame owner, Payment paymentToEdit) {
            super(owner, paymentToEdit == null ? "Add Payment" : "Edit Payment: " + paymentToEdit.getTransactionId(), true);
            this.paymentToEdit = paymentToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(520, 440);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout(15, 15));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            cmbCustomer = new JComboBox<>();
            loadCustomers();

            cmbPaymentMethod = new JComboBox<>(new String[]{"PayPal", "Transferencia", "Cheque", "Tarjeta de Credito"});
            txtTransactionId = new JTextField(15);
            txtPaymentDate = new JTextField(LocalDate.now().toString(), 15);
            txtTotal = new JTextField(15);

            int row = 0;
            addFormCombo(formPanel, gbc, "Customer *:", cmbCustomer, row++);
            addFormCombo(formPanel, gbc, "Payment Method *:", cmbPaymentMethod, row++);
            addFormField(formPanel, gbc, "Transaction ID *:", txtTransactionId, row++);
            addFormField(formPanel, gbc, "Payment Date (YYYY-MM-DD) *:", txtPaymentDate, row++);
            addFormField(formPanel, gbc, "Total Amount ($) *:", txtTotal, row++);

            if (paymentToEdit != null) {
                // Select customer
                for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
                    String item = cmbCustomer.getItemAt(i);
                    if (item.startsWith(paymentToEdit.getCustomerCode() + " -")) {
                        cmbCustomer.setSelectedIndex(i);
                        break;
                    }
                }
                cmbCustomer.setEnabled(false);
                txtTransactionId.setText(paymentToEdit.getTransactionId());
                txtTransactionId.setEnabled(false); // Composite primary key is immutable
                cmbPaymentMethod.setSelectedItem(paymentToEdit.getPaymentMethod());
                txtPaymentDate.setText(paymentToEdit.getPaymentDate() != null ? paymentToEdit.getPaymentDate().toString() : "");
                txtTotal.setText(paymentToEdit.getAmount() != null ? paymentToEdit.getAmount().toString() : "0.00");
            }

            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
            JButton btnSave = new JButton("Save");
            JButton btnCancel = new JButton("Cancel");

            Dimension actionBtnSize = new Dimension(90, 32);
            btnSave.setPreferredSize(actionBtnSize);
            btnCancel.setPreferredSize(actionBtnSize);

            btnSave.addActionListener(e -> onSave());
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnCancel);
            buttonPanel.add(btnSave);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void loadCustomers() {
            try {
                List<Customer> customers = customerUseCase.findAll();
                for (Customer c : customers) {
                    cmbCustomer.addItem(c.getCustomerCode() + " - " + c.getCustomerName());
                }
            } catch (Exception ignored) {
                // If customers failed to load, provide fallback
            }
            if (cmbCustomer.getItemCount() == 0) {
                cmbCustomer.setEditable(true);
            }
        }

        private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.40;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.60;
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(field, gbc);
        }

        private void addFormCombo(JPanel panel, GridBagConstraints gbc, String label, JComboBox<String> combo, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.40;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.60;
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(combo, gbc);
        }

        private void onSave() {
            try {
                Object selectedCustomer = cmbCustomer.getSelectedItem();
                if (selectedCustomer == null || selectedCustomer.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please select a customer.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String custStr = selectedCustomer.toString().trim();
                int customerCode;
                if (custStr.contains(" - ")) {
                    customerCode = Integer.parseInt(custStr.split(" - ")[0].trim());
                } else {
                    customerCode = Integer.parseInt(custStr);
                }

                String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();
                String transactionId = txtTransactionId.getText().trim();
                String dateStr = txtPaymentDate.getText().trim();
                String totalStr = txtTotal.getText().trim();

                if (transactionId.isEmpty() || dateStr.isEmpty() || totalStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields marked with *.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (transactionId.length() > 50) {
                    JOptionPane.showMessageDialog(this,
                            "Transaction ID cannot exceed 50 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LocalDate paymentDate;
                try {
                    paymentDate = LocalDate.parse(dateStr);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format. Please use YYYY-MM-DD (e.g. 2026-05-18).",
                            "Date Format Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal total = UIUtils.parseAmount(totalStr, "Total Amount", false);

                Payment payment = new Payment(
                        customerCode,
                        paymentMethod,
                        transactionId,
                        paymentDate,
                        total
                );

                if (paymentToEdit == null) {
                    paymentUseCase.save(payment);
                    JOptionPane.showMessageDialog(this,
                            "Payment recorded successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    paymentUseCase.update(payment);
                    JOptionPane.showMessageDialog(this,
                            "Payment updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, paymentToEdit == null ? "save" : "update", "Payment");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
