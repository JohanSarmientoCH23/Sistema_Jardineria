package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.OrderDetailUseCase;
import com.jardineria.application.usecases.OrderUseCase;
import com.jardineria.application.usecases.ProductUseCase;
import com.jardineria.domain.model.Order;
import com.jardineria.domain.model.OrderDetail;
import com.jardineria.domain.model.Product;
import com.jardineria.infrastructure.adapters.out.persistence.OrderDetailJdbcAdapter;
import com.jardineria.infrastructure.adapters.out.persistence.OrderJdbcAdapter;
import com.jardineria.infrastructure.adapters.out.persistence.ProductJdbcAdapter;

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
import java.util.List;

public class OrderDetailView extends JPanel {

    private final OrderDetailUseCase orderDetailUseCase = new OrderDetailUseCase(new OrderDetailJdbcAdapter());
    private final OrderUseCase orderUseCase = new OrderUseCase(new OrderJdbcAdapter());
    private final ProductUseCase productUseCase = new ProductUseCase(new ProductJdbcAdapter());

    private DefaultTableModel tableModel;
    private JTable orderDetailTable;

    public OrderDetailView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Top Panel: Title and Action Buttons (Add, Edit, Delete, Refresh)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Order Details (Line Items)");
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

        btnAdd.addActionListener(e -> showOrderDetailDialog(null));
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
        String[] columns = {"Order Code", "Product Code", "Quantity", "Unit Price", "Line Number"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderDetailTable = new JTable(tableModel);
        orderDetailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderDetailTable.setRowHeight(28);
        orderDetailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orderDetailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        orderDetailTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(orderDetailTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<OrderDetail> details = orderDetailUseCase.findAll();
            for (OrderDetail d : details) {
                tableModel.addRow(new Object[]{
                        d.getOrderCode(),
                        d.getProductCode(),
                        d.getQuantityOrdered(),
                        d.getPriceEach() != null ? String.format("$%.2f", d.getPriceEach()) : "$0.00",
                        d.getOrderLineNumber()
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Order Details");
        }
    }

    private void handleEdit() {
        int selectedRow = orderDetailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order line item from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderDetailTable.convertRowIndexToModel(selectedRow);
        Integer orderCode = (Integer) tableModel.getValueAt(modelRow, 0);
        String productCode = (String) tableModel.getValueAt(modelRow, 1);

        orderDetailUseCase.findById(orderCode, productCode).ifPresentOrElse(
                this::showOrderDetailDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected order detail not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = orderDetailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order line item from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderDetailTable.convertRowIndexToModel(selectedRow);
        Integer orderCode = (Integer) tableModel.getValueAt(modelRow, 0);
        String productCode = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete product '" + productCode + "' from order #" + orderCode + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderDetailUseCase.deleteById(orderCode, productCode);
                JOptionPane.showMessageDialog(this,
                        "Order detail line deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Order Detail");
            }
        }
    }

    private void showOrderDetailDialog(OrderDetail detailToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        OrderDetailFormDialog dialog = new OrderDetailFormDialog(owner, detailToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // Modal Form Dialog for Add / Edit
    private class OrderDetailFormDialog extends JDialog {
        private final OrderDetail detailToEdit;
        private boolean saved = false;

        private JComboBox<String> cmbOrderCode;
        private JComboBox<String> cmbProductCode;
        private JTextField txtQuantity;
        private JTextField txtUnitPrice;
        private JTextField txtLineNumber;

        public OrderDetailFormDialog(Frame owner, OrderDetail detailToEdit) {
            super(owner, detailToEdit == null ? "Add Order Detail" : "Edit Order Detail (#" + detailToEdit.getOrderCode() + " / " + detailToEdit.getProductCode() + ")", true);
            this.detailToEdit = detailToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(510, 440);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout(15, 15));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            cmbOrderCode = new JComboBox<>();
            loadOrders();

            cmbProductCode = new JComboBox<>();
            loadProducts();

            txtQuantity = new JTextField("1", 15);
            txtUnitPrice = new JTextField("0.00", 15);
            txtLineNumber = new JTextField("1", 15);

            int row = 0;
            addFormCombo(formPanel, gbc, "Order *:", cmbOrderCode, row++);
            addFormCombo(formPanel, gbc, "Product *:", cmbProductCode, row++);
            addFormField(formPanel, gbc, "Quantity Ordered *:", txtQuantity, row++);
            addFormField(formPanel, gbc, "Unit Price ($) *:", txtUnitPrice, row++);
            addFormField(formPanel, gbc, "Order Line Number *:", txtLineNumber, row++);

            if (detailToEdit != null) {
                // Select Order
                for (int i = 0; i < cmbOrderCode.getItemCount(); i++) {
                    if (cmbOrderCode.getItemAt(i).startsWith("Order #" + detailToEdit.getOrderCode())) {
                        cmbOrderCode.setSelectedIndex(i);
                        break;
                    }
                }
                cmbOrderCode.setEnabled(false);

                // Select Product
                for (int i = 0; i < cmbProductCode.getItemCount(); i++) {
                    if (cmbProductCode.getItemAt(i).startsWith(detailToEdit.getProductCode() + " -") ||
                        cmbProductCode.getItemAt(i).equals(detailToEdit.getProductCode())) {
                        cmbProductCode.setSelectedIndex(i);
                        break;
                    }
                }
                cmbProductCode.setEnabled(false);

                txtQuantity.setText(String.valueOf(detailToEdit.getQuantityOrdered()));
                txtUnitPrice.setText(detailToEdit.getPriceEach() != null ? detailToEdit.getPriceEach().toString() : "0.00");
                txtLineNumber.setText(detailToEdit.getOrderLineNumber() != null ? String.valueOf(detailToEdit.getOrderLineNumber()) : "1");
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

        private void loadOrders() {
            try {
                List<Order> orders = orderUseCase.findAll();
                for (Order o : orders) {
                    cmbOrderCode.addItem("Order #" + o.getOrderCode() + " (Customer " + o.getCustomerCode() + ")");
                }
            } catch (Exception ignored) {
            }
            if (cmbOrderCode.getItemCount() == 0) {
                cmbOrderCode.setEditable(true);
            }
        }

        private void loadProducts() {
            try {
                List<Product> products = productUseCase.findAll();
                for (Product p : products) {
                    cmbProductCode.addItem(p.getProductCode() + " - " + p.getName());
                }
            } catch (Exception ignored) {
            }
            if (cmbProductCode.getItemCount() == 0) {
                cmbProductCode.setEditable(true);
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
                Object selectedOrder = cmbOrderCode.getSelectedItem();
                Object selectedProduct = cmbProductCode.getSelectedItem();

                if (selectedOrder == null || selectedOrder.toString().trim().isEmpty() ||
                    selectedProduct == null || selectedProduct.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please select an Order and a Product.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String orderStr = selectedOrder.toString().trim();
                int orderCode;
                if (orderStr.startsWith("Order #")) {
                    orderCode = Integer.parseInt(orderStr.substring(7, orderStr.indexOf(" (")).trim());
                } else {
                    orderCode = Integer.parseInt(orderStr);
                }

                String prodStr = selectedProduct.toString().trim();
                String productCode;
                if (prodStr.contains(" - ")) {
                    productCode = prodStr.split(" - ")[0].trim();
                } else {
                    productCode = prodStr;
                }

                String qtyStr = txtQuantity.getText().trim();
                String priceStr = txtUnitPrice.getText().trim();
                String lineNumStr = txtLineNumber.getText().trim();

                if (qtyStr.isEmpty() || priceStr.isEmpty() || lineNumStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in Quantity, Price, and Line Number.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(qtyStr);
                    if (quantity <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Quantity must be a positive integer greater than zero.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal unitPrice = UIUtils.parseAmount(priceStr, "Unit Price", false);

                short lineNumber;
                try {
                    int parsedLine = Integer.parseInt(lineNumStr);
                    if (parsedLine <= 0 || parsedLine > Short.MAX_VALUE) throw new NumberFormatException();
                    lineNumber = (short) parsedLine;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Order Line Number must be between 1 and " + Short.MAX_VALUE + ".",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                OrderDetail detail = new OrderDetail(
                        orderCode,
                        productCode,
                        quantity,
                        unitPrice,
                        lineNumber
                );

                if (detailToEdit == null) {
                    orderDetailUseCase.save(detail);
                    JOptionPane.showMessageDialog(this,
                            "Order detail saved successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    orderDetailUseCase.update(detail);
                    JOptionPane.showMessageDialog(this,
                            "Order detail updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, detailToEdit == null ? "save" : "update", "Order Detail");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
