package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.ProductLineUseCase;
import com.jardineria.domain.model.ProductLine;
import com.jardineria.infrastructure.adapters.out.persistence.ProductLineJdbcAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
import java.util.List;

public class ProductLineView extends JPanel {

    private final ProductLineUseCase productLineUseCase = new ProductLineUseCase(new ProductLineJdbcAdapter());
    private DefaultTableModel tableModel;
    private JTable productLineTable;

    public ProductLineView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Top Panel: Title and Action Buttons (Add, Edit, Delete, Refresh)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Product Lines (Gamas)");
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

        btnAdd.addActionListener(e -> showProductLineDialog(null));
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
        String[] columns = {"Product Line", "Text Description", "HTML Description", "Image"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productLineTable = new JTable(tableModel);
        productLineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productLineTable.setRowHeight(28);
        productLineTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productLineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productLineTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(productLineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<ProductLine> lines = productLineUseCase.findAll();
            for (ProductLine pl : lines) {
                tableModel.addRow(new Object[]{
                        pl.getProductLine(),
                        pl.getTextDescription(),
                        pl.getHtmlDescription(),
                        pl.getImage()
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Product Lines");
        }
    }

    private void handleEdit() {
        int selectedRow = productLineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product line from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = productLineTable.convertRowIndexToModel(selectedRow);
        String productLine = (String) tableModel.getValueAt(modelRow, 0);

        productLineUseCase.findById(productLine).ifPresentOrElse(
                this::showProductLineDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected product line not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = productLineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product line from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = productLineTable.convertRowIndexToModel(selectedRow);
        String productLine = (String) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete product line '" + productLine + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productLineUseCase.deleteById(productLine);
                JOptionPane.showMessageDialog(this,
                        "Product line deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Product Line");
            }
        }
    }

    private void showProductLineDialog(ProductLine lineToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ProductLineFormDialog dialog = new ProductLineFormDialog(owner, lineToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // Modal Form Dialog for Add / Edit
    private class ProductLineFormDialog extends JDialog {
        private final ProductLine lineToEdit;
        private boolean saved = false;

        private JTextField txtProductLine;
        private JTextArea txtTextDescription;
        private JTextArea txtHtmlDescription;
        private JTextField txtImage;

        public ProductLineFormDialog(Frame owner, ProductLine lineToEdit) {
            super(owner, lineToEdit == null ? "Add Product Line" : "Edit Product Line: " + lineToEdit.getProductLine(), true);
            this.lineToEdit = lineToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(510, 520);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout(15, 15));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtProductLine = new JTextField(15);
            txtTextDescription = new JTextArea(3, 15);
            txtTextDescription.setLineWrap(true);
            txtTextDescription.setWrapStyleWord(true);
            JScrollPane scrollText = new JScrollPane(txtTextDescription);

            txtHtmlDescription = new JTextArea(3, 15);
            txtHtmlDescription.setLineWrap(true);
            txtHtmlDescription.setWrapStyleWord(true);
            JScrollPane scrollHtml = new JScrollPane(txtHtmlDescription);

            txtImage = new JTextField(15);

            int row = 0;
            addFormField(formPanel, gbc, "Product Line *:", txtProductLine, row++);

            // Text Description
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.38;
            formPanel.add(new JLabel("Text Description:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.62; gbc.fill = GridBagConstraints.BOTH;
            formPanel.add(scrollText, gbc);
            row++;

            // HTML Description
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.38;
            formPanel.add(new JLabel("HTML Description:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.62; gbc.fill = GridBagConstraints.BOTH;
            formPanel.add(scrollHtml, gbc);
            row++;

            gbc.fill = GridBagConstraints.HORIZONTAL;
            addFormField(formPanel, gbc, "Image (Path/Name):", txtImage, row++);

            if (lineToEdit != null) {
                txtProductLine.setText(lineToEdit.getProductLine());
                txtProductLine.setEnabled(false);
                txtTextDescription.setText(lineToEdit.getTextDescription() != null ? lineToEdit.getTextDescription() : "");
                txtHtmlDescription.setText(lineToEdit.getHtmlDescription() != null ? lineToEdit.getHtmlDescription() : "");
                txtImage.setText(lineToEdit.getImage() != null ? lineToEdit.getImage() : "");
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

        private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.38;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.62;
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(field, gbc);
        }

        private void onSave() {
            try {
                String line = txtProductLine.getText().trim();
                if (line.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Product Line is required.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (line.length() > 50) {
                    JOptionPane.showMessageDialog(this,
                            "Product Line name cannot exceed 50 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ProductLine productLine = new ProductLine(
                        line,
                        txtTextDescription.getText().trim(),
                        txtHtmlDescription.getText().trim(),
                        txtImage.getText().trim()
                );

                if (lineToEdit == null) {
                    productLineUseCase.save(productLine);
                    JOptionPane.showMessageDialog(this,
                            "Product line created successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    productLineUseCase.update(productLine);
                    JOptionPane.showMessageDialog(this,
                            "Product line updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, lineToEdit == null ? "save" : "update", "Product Line");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
