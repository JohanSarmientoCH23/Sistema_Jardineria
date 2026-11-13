package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.CustomerUseCase;
import com.jardineria.application.usecases.EmployeeUseCase;
import com.jardineria.domain.model.Customer;
import com.jardineria.domain.model.Employee;
import com.jardineria.infrastructure.adapters.out.persistence.CustomerJdbcAdapter;
import com.jardineria.infrastructure.adapters.out.persistence.EmployeeJdbcAdapter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.List;

public class CustomerView extends JPanel {

    // Corporate Palette
    private static final Color BG_CANVAS = new Color(245, 246, 248);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(226, 232, 240);
    private static final Color NAVY_PRIMARY = new Color(24, 38, 57);
    private static final Color TEXT_PRIMARY = new Color(24, 38, 57);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);

    private final CustomerUseCase customerUseCase = new CustomerUseCase(new CustomerJdbcAdapter());
    private final EmployeeUseCase employeeUseCase = new EmployeeUseCase(new EmployeeJdbcAdapter());
    private DefaultTableModel tableModel;
    private JTable customerTable;

    public CustomerView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_CANVAS);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. Top Header & Toolbar
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Table Card Container
        JPanel tableCard = createTableCard();
        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Titles
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("Customers Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Enterprise client accounts, contacts, regional locations & credit lines");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        titleBox.add(titleLabel);
        titleBox.add(Box.createRigidArea(new Dimension(0, 3)));
        titleBox.add(subtitleLabel);

        // Action Buttons Toolbar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Add Customer", NAVY_PRIMARY, Color.WHITE, true);
        JButton btnEdit = createStyledButton("Edit", CARD_BG, TEXT_PRIMARY, false);
        JButton btnDelete = createStyledButton("Delete", CARD_BG, new Color(220, 38, 38), false);
        JButton btnRefresh = createStyledButton("Refresh", CARD_BG, TEXT_PRIMARY, false);

        btnAdd.addActionListener(e -> showCustomerDialog(null));
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnRefresh.addActionListener(e -> loadData());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnAdd);

        topPanel.add(titleBox, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JButton createStyledButton(String text, Color bg, Color fg, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(isPrimary ? 120 : 85, 34));

        if (!isPrimary) {
            btn.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
        } else {
            btn.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        }

        return btn;
    }

    private JPanel createTableCard() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        String[] columns = {"Code", "Company Name", "Contact Person", "Phone", "City", "Country", "Credit Limit"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setRowHeight(35);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setShowHorizontalLines(true);
        customerTable.setShowVerticalLines(false);
        customerTable.setGridColor(new Color(241, 245, 249));

        // Format Credit Limit column as a green badge
        customerTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setForeground(new Color(22, 101, 52));
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Customer> customers = customerUseCase.findAll();
            for (Customer c : customers) {
                String contact = (c.getContactName() != null ? c.getContactName() : "") +
                        (c.getContactLastName() != null && !c.getContactLastName().isBlank() ? " " + c.getContactLastName() : "");

                tableModel.addRow(new Object[]{
                        c.getCustomerCode(),
                        c.getCustomerName(),
                        contact.trim(),
                        c.getPhone(),
                        c.getCity(),
                        c.getCountry(),
                        c.getCreditLimit() != null ? String.format("$%.2f", c.getCreditLimit()) : "$0.00"
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Customers");
        }
    }

    private void handleEdit() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = customerTable.convertRowIndexToModel(selectedRow);
        Integer customerCode = (Integer) tableModel.getValueAt(modelRow, 0);

        customerUseCase.findById(customerCode).ifPresentOrElse(
                this::showCustomerDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected customer not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = customerTable.convertRowIndexToModel(selectedRow);
        Integer customerCode = (Integer) tableModel.getValueAt(modelRow, 0);
        String customerName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete customer #" + customerCode + " (" + customerName + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerUseCase.deleteById(customerCode);
                JOptionPane.showMessageDialog(this,
                        "Customer deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Customer");
            }
        }
    }

    private void showCustomerDialog(Customer customerToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CustomerFormDialog dialog = new CustomerFormDialog(owner, customerToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // ==========================================
    // Corporate Modal Form Dialog (Add / Edit)
    // ==========================================
    private class CustomerFormDialog extends JDialog {
        private final Customer customerToEdit;
        private boolean saved = false;

        private JTextField txtCode;
        private JTextField txtName;
        private JTextField txtContactName;
        private JTextField txtContactLastName;
        private JTextField txtPhone;
        private JTextField txtFax;
        private JTextField txtAddressLine1;
        private JTextField txtCity;
        private JTextField txtCountry;
        private JComboBox<String> cmbSalesRep;
        private JTextField txtCreditLimit;

        public CustomerFormDialog(Frame owner, Customer customerToEdit) {
            super(owner, customerToEdit == null ? "Add New Customer" : "Edit Customer #" + customerToEdit.getCustomerCode(), true);
            this.customerToEdit = customerToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(530, 580);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout());

            // Header Banner
            JPanel headerBanner = new JPanel(new BorderLayout());
            headerBanner.setBackground(NAVY_PRIMARY);
            headerBanner.setBorder(new EmptyBorder(16, 24, 16, 24));

            JLabel title = new JLabel(customerToEdit == null ? "Add New Customer Account" : "Update Customer Record");
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            title.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel(customerToEdit == null ? "Fill in the required enterprise client details" : "Modify existing client parameters and credit threshold");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(203, 213, 225));

            headerBanner.add(title, BorderLayout.NORTH);
            headerBanner.add(subtitle, BorderLayout.SOUTH);
            add(headerBanner, BorderLayout.NORTH);

            // Form Body
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(new EmptyBorder(18, 25, 12, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 6, 5, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtCode = createStyledTextField();
            txtName = createStyledTextField();
            txtContactName = createStyledTextField();
            txtContactLastName = createStyledTextField();
            txtPhone = createStyledTextField();
            txtFax = createStyledTextField();
            txtAddressLine1 = createStyledTextField();
            txtCity = createStyledTextField();
            txtCountry = createStyledTextField();
            cmbSalesRep = new JComboBox<>();
            cmbSalesRep.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            loadSalesReps();
            txtCreditLimit = createStyledTextField();
            txtCreditLimit.setText("0.00");

            int row = 0;
            addFormField(formPanel, gbc, "Customer Code *:", txtCode, row++);
            addFormField(formPanel, gbc, "Company Name *:", txtName, row++);
            addFormField(formPanel, gbc, "Contact First Name:", txtContactName, row++);
            addFormField(formPanel, gbc, "Contact Last Name:", txtContactLastName, row++);
            addFormField(formPanel, gbc, "Phone *:", txtPhone, row++);
            addFormField(formPanel, gbc, "Fax:", txtFax, row++);
            addFormField(formPanel, gbc, "Address Line *:", txtAddressLine1, row++);
            addFormField(formPanel, gbc, "City *:", txtCity, row++);
            addFormField(formPanel, gbc, "Country *:", txtCountry, row++);
            addFormCombo(formPanel, gbc, "Sales Rep:", cmbSalesRep, row++);
            addFormField(formPanel, gbc, "Credit Limit ($):", txtCreditLimit, row++);

            if (customerToEdit != null) {
                txtCode.setText(String.valueOf(customerToEdit.getCustomerCode()));
                txtCode.setEnabled(false);
                txtName.setText(customerToEdit.getCustomerName());
                txtContactName.setText(customerToEdit.getContactName() != null ? customerToEdit.getContactName() : "");
                txtContactLastName.setText(customerToEdit.getContactLastName() != null ? customerToEdit.getContactLastName() : "");
                txtPhone.setText(customerToEdit.getPhone());
                txtFax.setText(customerToEdit.getFax() != null ? customerToEdit.getFax() : "");
                txtAddressLine1.setText(customerToEdit.getAddressLine1());
                txtCity.setText(customerToEdit.getCity());
                txtCountry.setText(customerToEdit.getCountry() != null ? customerToEdit.getCountry() : "");

                if (customerToEdit.getSalesRepEmployeeCode() != null) {
                    for (int i = 0; i < cmbSalesRep.getItemCount(); i++) {
                        if (cmbSalesRep.getItemAt(i).startsWith(customerToEdit.getSalesRepEmployeeCode() + " -")) {
                            cmbSalesRep.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    cmbSalesRep.setSelectedIndex(0);
                }

                txtCreditLimit.setText(customerToEdit.getCreditLimit() != null ? customerToEdit.getCreditLimit().toString() : "0.00");
            }

            add(formPanel, BorderLayout.CENTER);

            // Footer Action Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
            buttonPanel.setBackground(new Color(248, 250, 252));
            buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnCancel.setPreferredSize(new Dimension(85, 33));
            btnCancel.setBackground(Color.WHITE);
            btnCancel.setForeground(TEXT_PRIMARY);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));

            JButton btnSave = new JButton("Save Record");
            btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnSave.setPreferredSize(new Dimension(110, 33));
            btnSave.setBackground(NAVY_PRIMARY);
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            btnSave.addActionListener(e -> onSave());
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnCancel);
            buttonPanel.add(btnSave);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private JTextField createStyledTextField() {
            JTextField tf = new JTextField(15);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setMargin(new Insets(4, 8, 4, 8));
            return tf;
        }

        private void loadSalesReps() {
            cmbSalesRep.addItem("None (No Sales Rep Assigned)");
            try {
                List<Employee> employees = employeeUseCase.findAll();
                for (Employee emp : employees) {
                    cmbSalesRep.addItem(emp.getEmployeeCode() + " - " + emp.getFirstName() + " " + emp.getLastName1());
                }
            } catch (Exception ignored) {
            }
        }

        private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.38;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(51, 65, 85));
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.62;
            panel.add(field, gbc);
        }

        private void addFormCombo(JPanel panel, GridBagConstraints gbc, String label, JComboBox<String> combo, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.38;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(51, 65, 85));
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.62;
            panel.add(combo, gbc);
        }

        private void onSave() {
            try {
                String codeStr = txtCode.getText().trim();
                String name = txtName.getText().trim();
                String phone = txtPhone.getText().trim();
                String address = txtAddressLine1.getText().trim();
                String city = txtCity.getText().trim();
                String country = txtCountry.getText().trim();

                if (codeStr.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || country.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields (Code, Company Name, Phone, Address, City, Country).",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int code;
                try {
                    code = Integer.parseInt(codeStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Customer Code must be a valid integer number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (phone.length() > 15) {
                    JOptionPane.showMessageDialog(this,
                            "Phone number cannot exceed 15 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String fax = txtFax.getText().trim();
                if (fax.length() > 15) {
                    JOptionPane.showMessageDialog(this,
                            "Fax number cannot exceed 15 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Integer salesRep = null;
                Object selectedRep = cmbSalesRep.getSelectedItem();
                if (selectedRep != null && !selectedRep.toString().startsWith("None")) {
                    salesRep = Integer.parseInt(selectedRep.toString().split(" - ")[0].trim());
                }

                BigDecimal creditLimit = UIUtils.parseAmount(txtCreditLimit.getText(), "Credit Limit", true);

                Customer customer = new Customer(
                        code,
                        name,
                        txtContactName.getText().trim(),
                        txtContactLastName.getText().trim(),
                        phone,
                        fax,
                        address,
                        city,
                        country,
                        salesRep,
                        creditLimit
                );

                if (customerToEdit == null) {
                    customerUseCase.save(customer);
                    JOptionPane.showMessageDialog(this,
                            "Customer saved successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    customerUseCase.update(customer);
                    JOptionPane.showMessageDialog(this,
                            "Customer updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, customerToEdit == null ? "save" : "update", "Customer");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.setColor(CARD_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
