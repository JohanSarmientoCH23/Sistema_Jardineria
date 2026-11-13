package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.ports.in.CustomerServicePort;
import com.jardineria.application.usecases.CustomerUseCase;
import com.jardineria.domain.model.Customer;
import com.jardineria.infrastructure.adapters.out.persistence.CustomerJdbcAdapter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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

public class CustomerView extends JPanel {

    private final CustomerServicePort customerUseCase;
    private DefaultTableModel tableModel;
    private JTable customerTable;

    public CustomerView() {
        this.customerUseCase = new CustomerUseCase(new CustomerJdbcAdapter());
        initComponents();
        loadCustomersData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // North Panel: Title and Action Buttons
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("Customers Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitleLabel = new JLabel("View, create, update and delete customer accounts");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        // Styling buttons
        Dimension btnSize = new Dimension(95, 34);
        btnRefresh.setPreferredSize(btnSize);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);

        btnRefresh.addActionListener(e -> loadCustomersData());
        btnAdd.addActionListener(e -> showCustomerFormDialog(null));
        btnEdit.addActionListener(e -> handleEditCustomer());
        btnDelete.addActionListener(e -> handleDeleteCustomer());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        northPanel.add(titlePanel, BorderLayout.WEST);
        northPanel.add(buttonPanel, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);

        // Center Panel: Table inside ScrollPane
        String[] columnNames = {
                "Code", "Name", "Contact Name", "Contact Last Name",
                "Phone", "City", "Country", "Credit Limit"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setRowHeight(28);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCustomersData() {
        tableModel.setRowCount(0);
        try {
            List<Customer> customers = customerUseCase.findAll();
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                        c.getCustomerCode(),
                        c.getCustomerName(),
                        c.getContactName(),
                        c.getContactLastName(),
                        c.getPhone(),
                        c.getCity(),
                        c.getCountry(),
                        c.getCreditLimit() != null ? String.format("$%.2f", c.getCreditLimit()) : "$0.00"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load customer data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = customerTable.convertRowIndexToModel(selectedRow);
        Integer customerCode = (Integer) tableModel.getValueAt(modelRow, 0);

        customerUseCase.findById(customerCode).ifPresentOrElse(
                this::showCustomerFormDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Customer record not found.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDeleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
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
                loadCustomersData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting customer: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCustomerFormDialog(Customer customerToEdit) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CustomerFormDialog dialog = new CustomerFormDialog(parentFrame, customerToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadCustomersData();
        }
    }

    // Reusable Form Dialog for Add and Edit
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
        private JTextField txtSalesRep;
        private JTextField txtCreditLimit;

        public CustomerFormDialog(Frame owner, Customer customerToEdit) {
            super(owner, customerToEdit == null ? "Add New Customer" : "Edit Customer #" + customerToEdit.getCustomerCode(), true);
            this.customerToEdit = customerToEdit;
            initComponents();
        }

        private void initComponents() {
            setSize(500, 560);
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
            txtContactName = new JTextField(15);
            txtContactLastName = new JTextField(15);
            txtPhone = new JTextField(15);
            txtFax = new JTextField(15);
            txtAddressLine1 = new JTextField(15);
            txtCity = new JTextField(15);
            txtCountry = new JTextField(15);
            txtSalesRep = new JTextField(15);
            txtCreditLimit = new JTextField(15);

            int row = 0;
            addFormField(formPanel, gbc, "Customer Code *:", txtCode, row++);
            addFormField(formPanel, gbc, "Company Name *:", txtName, row++);
            addFormField(formPanel, gbc, "Contact First Name:", txtContactName, row++);
            addFormField(formPanel, gbc, "Contact Last Name:", txtContactLastName, row++);
            addFormField(formPanel, gbc, "Phone Number *:", txtPhone, row++);
            addFormField(formPanel, gbc, "Fax Number:", txtFax, row++);
            addFormField(formPanel, gbc, "Address Line 1 *:", txtAddressLine1, row++);
            addFormField(formPanel, gbc, "City *:", txtCity, row++);
            addFormField(formPanel, gbc, "Country *:", txtCountry, row++);
            addFormField(formPanel, gbc, "Sales Rep Code:", txtSalesRep, row++);
            addFormField(formPanel, gbc, "Credit Limit ($):", txtCreditLimit, row++);

            // Pre-fill fields if editing
            if (customerToEdit != null) {
                txtCode.setText(String.valueOf(customerToEdit.getCustomerCode()));
                txtCode.setEnabled(false); // Code cannot be modified in Edit mode
                txtName.setText(customerToEdit.getCustomerName());
                txtContactName.setText(customerToEdit.getContactName());
                txtContactLastName.setText(customerToEdit.getContactLastName());
                txtPhone.setText(customerToEdit.getPhone());
                txtFax.setText(customerToEdit.getFax());
                txtAddressLine1.setText(customerToEdit.getAddressLine1());
                txtCity.setText(customerToEdit.getCity());
                txtCountry.setText(customerToEdit.getCountry());
                txtSalesRep.setText(customerToEdit.getSalesRepEmployeeCode() != null ? String.valueOf(customerToEdit.getSalesRepEmployeeCode()) : "");
                txtCreditLimit.setText(customerToEdit.getCreditLimit() != null ? customerToEdit.getCreditLimit().toString() : "0.00");
            }

            add(formPanel, BorderLayout.CENTER);

            // Dialog Action Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
            JButton btnSave = new JButton("Save");
            JButton btnCancel = new JButton("Cancel");

            btnSave.setPreferredSize(new Dimension(85, 32));
            btnCancel.setPreferredSize(new Dimension(85, 32));

            btnSave.addActionListener(e -> saveCustomer());
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnCancel);
            buttonPanel.add(btnSave);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.35;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.65;
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(field, gbc);
        }

        private void saveCustomer() {
            try {
                // Validation
                String codeStr = txtCode.getText().trim();
                String name = txtName.getText().trim();
                String phone = txtPhone.getText().trim();
                String address = txtAddressLine1.getText().trim();
                String city = txtCity.getText().trim();
                String country = txtCountry.getText().trim();

                if (codeStr.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || country.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields (Code, Name, Phone, Address, City, Country).",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int code = Integer.parseInt(codeStr);

                Integer salesRep = null;
                if (!txtSalesRep.getText().trim().isEmpty()) {
                    salesRep = Integer.parseInt(txtSalesRep.getText().trim());
                }

                BigDecimal creditLimit = BigDecimal.ZERO;
                if (!txtCreditLimit.getText().trim().isEmpty()) {
                    creditLimit = new BigDecimal(txtCreditLimit.getText().trim());
                }

                Customer customer = new Customer(
                        code,
                        name,
                        txtContactName.getText().trim(),
                        txtContactLastName.getText().trim(),
                        phone,
                        txtFax.getText().trim(),
                        address,
                        city,
                        country,
                        salesRep,
                        creditLimit
                );

                if (customerToEdit == null) {
                    customerUseCase.save(customer);
                    JOptionPane.showMessageDialog(this,
                            "Customer created successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    customerUseCase.update(customer);
                    JOptionPane.showMessageDialog(this,
                            "Customer updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid number format in Code, Sales Rep or Credit Limit.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving customer: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
