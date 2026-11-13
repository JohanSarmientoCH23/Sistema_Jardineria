package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.ProductLineUseCase;
import com.jardineria.application.usecases.ProductUseCase;
import com.jardineria.domain.model.Product;
import com.jardineria.domain.model.ProductLine;
import com.jardineria.infrastructure.adapters.out.persistence.ProductJdbcAdapter;
import com.jardineria.infrastructure.adapters.out.persistence.ProductLineJdbcAdapter;

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

public class ProductView extends JPanel {

    private final ProductUseCase productUseCase = new ProductUseCase(new ProductJdbcAdapter());
    private final ProductLineUseCase productLineUseCase = new ProductLineUseCase(new ProductLineJdbcAdapter());
    private DefaultTableModel tableModel;
    private JTable productTable;

    public ProductView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Top Panel: Title and Action Buttons (Add, Edit, Delete, Refresh)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Product Catalog");
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

        btnAdd.addActionListener(e -> showProductDialog(null));
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
        String[] columns = {"Code", "Name", "Product Line", "Stock", "Buy Price", "MSRP"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(28);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Product> products = productUseCase.findAll();
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                        p.getProductCode(),
                        p.getName(),
                        p.getProductLine(),
                        p.getQuantityInStock(),
                        p.getBuyPrice() != null ? String.format("$%.2f", p.getBuyPrice()) : "$0.00",
                        p.getMsrp() != null ? String.format("$%.2f", p.getMsrp()) : "$0.00"
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Products");
        }
    }

    private void handleEdit() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = productTable.convertRowIndexToModel(selectedRow);
        String productCode = (String) tableModel.getValueAt(modelRow, 0);

        productUseCase.findById(productCode).ifPresentOrElse(
                this::showProductDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected product not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = productTable.convertRowIndexToModel(selectedRow);
        String productCode = (String) tableModel.getValueAt(modelRow, 0);
        String productName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete product '" + productCode + "' (" + productName + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productUseCase.deleteById(productCode);
                JOptionPane.showMessageDialog(this,
                        "Product deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Product");
            }
        }
    }

    private void showProductDialog(Product productToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ProductFormDialog dialog = new ProductFormDialog(owner, productToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // Modal Form Dialog for Add / Edit
    private class ProductFormDialog extends JDialog {
        private final Product productToEdit;
        private boolean saved = false;

        private JTextField txtCode;
        private JTextField txtName;
        private JComboBox<String> cmbProductLine;
        private JTextField txtDimensions;
        private JTextField txtSupplier;
        private JTextField txtDescription;
        private JTextField txtStock;
        private JTextField txtBuyPrice;
        private JTextField txtMsrp;

        public ProductFormDialog(Frame owner, Product productToEdit) {
            super(owner, productToEdit == null ? "Add Product" : "Edit Product: " + productToEdit.getProductCode(), true);
            this.productToEdit = productToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(510, 550);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout(15, 15));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtCode = new JTextField(15);
            txtName = new JTextField(15);
            cmbProductLine = new JComboBox<>();
            loadProductLines();

            txtDimensions = new JTextField(15);
            txtSupplier = new JTextField(15);
            txtDescription = new JTextField(15);
            txtStock = new JTextField("0", 15);
            txtBuyPrice = new JTextField("0.00", 15);
            txtMsrp = new JTextField("0.00", 15);

            int row = 0;
            addFormField(formPanel, gbc, "Product Code *:", txtCode, row++);
            addFormField(formPanel, gbc, "Product Name *:", txtName, row++);
            addFormCombo(formPanel, gbc, "Product Line (Gama) *:", cmbProductLine, row++);
            addFormField(formPanel, gbc, "Dimensions:", txtDimensions, row++);
            addFormField(formPanel, gbc, "Supplier *:", txtSupplier, row++);
            addFormField(formPanel, gbc, "Description:", txtDescription, row++);
            addFormField(formPanel, gbc, "Quantity in Stock *:", txtStock, row++);
            addFormField(formPanel, gbc, "Buy Price ($):", txtBuyPrice, row++);
            addFormField(formPanel, gbc, "MSRP (Sale Price $) *:", txtMsrp, row++);

            if (productToEdit != null) {
                txtCode.setText(productToEdit.getProductCode());
                txtCode.setEnabled(false);
                txtName.setText(productToEdit.getName());
                cmbProductLine.setSelectedItem(productToEdit.getProductLine());
                txtDimensions.setText(productToEdit.getDimensions() != null ? productToEdit.getDimensions() : "");
                txtSupplier.setText(productToEdit.getSupplier() != null ? productToEdit.getSupplier() : "");
                txtDescription.setText(productToEdit.getDescription() != null ? productToEdit.getDescription() : "");
                txtStock.setText(productToEdit.getQuantityInStock() != null ? String.valueOf(productToEdit.getQuantityInStock()) : "0");
                txtBuyPrice.setText(productToEdit.getBuyPrice() != null ? productToEdit.getBuyPrice().toString() : "0.00");
                txtMsrp.setText(productToEdit.getMsrp() != null ? productToEdit.getMsrp().toString() : "0.00");
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

        private void loadProductLines() {
            try {
                List<ProductLine> lines = productLineUseCase.findAll();
                for (ProductLine pl : lines) {
                    cmbProductLine.addItem(pl.getProductLine());
                }
            } catch (Exception ignored) {
            }
            if (cmbProductLine.getItemCount() == 0) {
                cmbProductLine.addItem("Aromáticas");
                cmbProductLine.addItem("Frutales");
                cmbProductLine.addItem("Herbaceas");
                cmbProductLine.addItem("Herramientas");
                cmbProductLine.addItem("Ornamentales");
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
                String code = txtCode.getText().trim();
                String name = txtName.getText().trim();
                String productLine = cmbProductLine.getSelectedItem() != null ? cmbProductLine.getSelectedItem().toString().trim() : "";
                String supplier = txtSupplier.getText().trim();
                String stockStr = txtStock.getText().trim();
                String buyPriceStr = txtBuyPrice.getText().trim();
                String msrpStr = txtMsrp.getText().trim();

                if (code.isEmpty() || name.isEmpty() || productLine.isEmpty() || supplier.isEmpty() ||
                        stockStr.isEmpty() || msrpStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields marked with * (Code, Name, Product Line, Supplier, Stock, MSRP).",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (code.length() > 15) {
                    JOptionPane.showMessageDialog(this,
                            "Product Code cannot exceed 15 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                short stock;
                try {
                    int parsedStock = Integer.parseInt(stockStr);
                    if (parsedStock < 0 || parsedStock > Short.MAX_VALUE) {
                        throw new NumberFormatException();
                    }
                    stock = (short) parsedStock;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Stock must be a positive whole number between 0 and " + Short.MAX_VALUE + ".",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal buyPrice = UIUtils.parseAmount(buyPriceStr, "Buy Price", true);
                BigDecimal msrp = UIUtils.parseAmount(msrpStr, "MSRP (Sale Price)", false);

                Product product = new Product(
                        code,
                        name,
                        productLine,
                        txtDimensions.getText().trim(),
                        supplier,
                        txtDescription.getText().trim(),
                        stock,
                        buyPrice,
                        msrp
                );

                if (productToEdit == null) {
                    productUseCase.save(product);
                    JOptionPane.showMessageDialog(this,
                            "Product created successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    productUseCase.update(product);
                    JOptionPane.showMessageDialog(this,
                            "Product updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, productToEdit == null ? "save" : "update", "Product");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
