package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.CustomerUseCase;
import com.jardineria.application.usecases.OrderUseCase;
import com.jardineria.domain.model.Customer;
import com.jardineria.domain.model.Order;
import com.jardineria.infrastructure.adapters.out.persistence.CustomerJdbcAdapter;
import com.jardineria.infrastructure.adapters.out.persistence.OrderJdbcAdapter;

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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class OrderView extends JPanel {

    private final OrderUseCase orderUseCase = new OrderUseCase(new OrderJdbcAdapter());
    private final CustomerUseCase customerUseCase = new CustomerUseCase(new CustomerJdbcAdapter());
    private DefaultTableModel tableModel;
    private JTable orderTable;

    public OrderView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Top Panel: Title and Action Buttons (Add, Edit, Delete, Refresh)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Orders Processing");
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

        btnAdd.addActionListener(e -> showOrderDialog(null));
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
        String[] columns = {"Code", "Order Date", "Required Date", "Shipped Date", "Status", "Customer Code"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(28);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        orderTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Order> orders = orderUseCase.findAll();
            for (Order o : orders) {
                tableModel.addRow(new Object[]{
                        o.getOrderCode(),
                        o.getOrderDate() != null ? o.getOrderDate().toString() : "",
                        o.getRequiredDate() != null ? o.getRequiredDate().toString() : "",
                        o.getShippedDate() != null ? o.getShippedDate().toString() : "",
                        o.getStatus(),
                        o.getCustomerCode()
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Orders");
        }
    }

    private void handleEdit() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        Integer orderCode = (Integer) tableModel.getValueAt(modelRow, 0);

        orderUseCase.findById(orderCode).ifPresentOrElse(
                this::showOrderDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected order not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        Integer orderCode = (Integer) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete order #" + orderCode + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderUseCase.deleteById(orderCode);
                JOptionPane.showMessageDialog(this,
                        "Order deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Order");
            }
        }
    }

    private void showOrderDialog(Order orderToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        OrderFormDialog dialog = new OrderFormDialog(owner, orderToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // Modal Form Dialog for Add / Edit
    private class OrderFormDialog extends JDialog {
        private final Order orderToEdit;
        private boolean saved = false;

        private JTextField txtOrderCode;
        private JTextField txtOrderDate;
        private JTextField txtRequiredDate;
        private JTextField txtShippedDate;
        private JComboBox<String> cmbStatus;
        private JTextField txtComments;
        private JComboBox<String> cmbCustomer;

        public OrderFormDialog(Frame owner, Order orderToEdit) {
            super(owner, orderToEdit == null ? "Add Order" : "Edit Order #" + orderToEdit.getOrderCode(), true);
            this.orderToEdit = orderToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(510, 480);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout(15, 15));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtOrderCode = new JTextField(15);
            txtOrderDate = new JTextField(LocalDate.now().toString(), 15);
            txtRequiredDate = new JTextField(LocalDate.now().plusDays(7).toString(), 15);
            txtShippedDate = new JTextField(15);
            cmbStatus = new JComboBox<>(new String[]{"Pendiente", "Entregado", "Rechazado"});
            txtComments = new JTextField(15);
            cmbCustomer = new JComboBox<>();
            loadCustomers();

            int row = 0;
            addFormField(formPanel, gbc, "Order Code *:", txtOrderCode, row++);
            addFormField(formPanel, gbc, "Order Date (YYYY-MM-DD) *:", txtOrderDate, row++);
            addFormField(formPanel, gbc, "Required Date (YYYY-MM-DD) *:", txtRequiredDate, row++);
            addFormField(formPanel, gbc, "Shipped Date (YYYY-MM-DD):", txtShippedDate, row++);
            addFormCombo(formPanel, gbc, "Status *:", cmbStatus, row++);
            addFormField(formPanel, gbc, "Comments:", txtComments, row++);
            addFormCombo(formPanel, gbc, "Customer *:", cmbCustomer, row++);

            if (orderToEdit != null) {
                txtOrderCode.setText(String.valueOf(orderToEdit.getOrderCode()));
                txtOrderCode.setEnabled(false);
                txtOrderDate.setText(orderToEdit.getOrderDate() != null ? orderToEdit.getOrderDate().toString() : "");
                txtRequiredDate.setText(orderToEdit.getRequiredDate() != null ? orderToEdit.getRequiredDate().toString() : "");
                txtShippedDate.setText(orderToEdit.getShippedDate() != null ? orderToEdit.getShippedDate().toString() : "");
                cmbStatus.setSelectedItem(orderToEdit.getStatus());
                txtComments.setText(orderToEdit.getComments() != null ? orderToEdit.getComments() : "");

                for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
                    if (cmbCustomer.getItemAt(i).startsWith(orderToEdit.getCustomerCode() + " -")) {
                        cmbCustomer.setSelectedIndex(i);
                        break;
                    }
                }
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
                String codeStr = txtOrderCode.getText().trim();
                String orderDateStr = txtOrderDate.getText().trim();
                String requiredDateStr = txtRequiredDate.getText().trim();
                String shippedDateStr = txtShippedDate.getText().trim();
                String status = (String) cmbStatus.getSelectedItem();
                Object selectedCustomer = cmbCustomer.getSelectedItem();

                if (codeStr.isEmpty() || orderDateStr.isEmpty() || requiredDateStr.isEmpty() || selectedCustomer == null) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields marked with *.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int code;
                try {
                    code = Integer.parseInt(codeStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Order Code must be a valid integer number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String custStr = selectedCustomer.toString().trim();
                int customerCode;
                if (custStr.contains(" - ")) {
                    customerCode = Integer.parseInt(custStr.split(" - ")[0].trim());
                } else {
                    customerCode = Integer.parseInt(custStr);
                }

                LocalDate orderDate;
                LocalDate requiredDate;
                LocalDate shippedDate = null;
                try {
                    orderDate = LocalDate.parse(orderDateStr);
                    requiredDate = LocalDate.parse(requiredDateStr);
                    if (!shippedDateStr.isEmpty()) {
                        shippedDate = LocalDate.parse(shippedDateStr);
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format. Please use YYYY-MM-DD (e.g. 2026-05-18).",
                            "Date Format Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Order order = new Order(
                        code,
                        orderDate,
                        requiredDate,
                        shippedDate,
                        status,
                        txtComments.getText().trim(),
                        customerCode
                );

                if (orderToEdit == null) {
                    orderUseCase.save(order);
                    JOptionPane.showMessageDialog(this,
                            "Order created successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    orderUseCase.update(order);
                    JOptionPane.showMessageDialog(this,
                            "Order updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, orderToEdit == null ? "save" : "update", "Order");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
