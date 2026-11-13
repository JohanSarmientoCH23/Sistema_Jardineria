package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.OfficeUseCase;
import com.jardineria.domain.model.Office;
import com.jardineria.infrastructure.adapters.out.persistence.OfficeJdbcAdapter;

import javax.swing.BorderFactory;
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
import java.util.List;

public class OfficeView extends JPanel {

    private final OfficeUseCase officeUseCase = new OfficeUseCase(new OfficeJdbcAdapter());
    private DefaultTableModel tableModel;
    private JTable officeTable;

    public OfficeView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Top Panel: Title and Action Buttons (Add, Edit, Delete, Refresh)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Offices Management");
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

        btnAdd.addActionListener(e -> showOfficeDialog(null));
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
        String[] columns = {"Office Code", "City", "Country", "Region", "Postal Code", "Phone"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        officeTable = new JTable(tableModel);
        officeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        officeTable.setRowHeight(28);
        officeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        officeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        officeTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(officeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Office> offices = officeUseCase.findAll();
            for (Office o : offices) {
                tableModel.addRow(new Object[]{
                        o.getOfficeCode(),
                        o.getCity(),
                        o.getCountry(),
                        o.getRegion(),
                        o.getPostalCode(),
                        o.getPhone()
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Offices");
        }
    }

    private void handleEdit() {
        int selectedRow = officeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an office from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = officeTable.convertRowIndexToModel(selectedRow);
        String officeCode = (String) tableModel.getValueAt(modelRow, 0);

        officeUseCase.findById(officeCode).ifPresentOrElse(
                this::showOfficeDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected office not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = officeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an office from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = officeTable.convertRowIndexToModel(selectedRow);
        String officeCode = (String) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete office '" + officeCode + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                officeUseCase.deleteById(officeCode);
                JOptionPane.showMessageDialog(this,
                        "Office deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Office");
            }
        }
    }

    private void showOfficeDialog(Office officeToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        OfficeFormDialog dialog = new OfficeFormDialog(owner, officeToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // Modal Form Dialog for Add / Edit
    private class OfficeFormDialog extends JDialog {
        private final Office officeToEdit;
        private boolean saved = false;

        private JTextField txtOfficeCode;
        private JTextField txtCity;
        private JTextField txtCountry;
        private JTextField txtRegion;
        private JTextField txtPostalCode;
        private JTextField txtPhone;
        private JTextField txtAddressLine1;
        private JTextField txtAddressLine2;

        public OfficeFormDialog(Frame owner, Office officeToEdit) {
            super(owner, officeToEdit == null ? "Add Office" : "Edit Office: " + officeToEdit.getOfficeCode(), true);
            this.officeToEdit = officeToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(510, 500);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout(15, 15));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtOfficeCode = new JTextField(15);
            txtCity = new JTextField(15);
            txtCountry = new JTextField(15);
            txtRegion = new JTextField(15);
            txtPostalCode = new JTextField(15);
            txtPhone = new JTextField(15);
            txtAddressLine1 = new JTextField(15);
            txtAddressLine2 = new JTextField(15);

            int row = 0;
            addFormField(formPanel, gbc, "Office Code *:", txtOfficeCode, row++);
            addFormField(formPanel, gbc, "City *:", txtCity, row++);
            addFormField(formPanel, gbc, "Country *:", txtCountry, row++);
            addFormField(formPanel, gbc, "Region:", txtRegion, row++);
            addFormField(formPanel, gbc, "Postal Code *:", txtPostalCode, row++);
            addFormField(formPanel, gbc, "Phone *:", txtPhone, row++);
            addFormField(formPanel, gbc, "Address Line 1 *:", txtAddressLine1, row++);
            addFormField(formPanel, gbc, "Address Line 2:", txtAddressLine2, row++);

            if (officeToEdit != null) {
                txtOfficeCode.setText(officeToEdit.getOfficeCode());
                txtOfficeCode.setEnabled(false);
                txtCity.setText(officeToEdit.getCity());
                txtCountry.setText(officeToEdit.getCountry());
                txtRegion.setText(officeToEdit.getRegion() != null ? officeToEdit.getRegion() : "");
                txtPostalCode.setText(officeToEdit.getPostalCode());
                txtPhone.setText(officeToEdit.getPhone());
                txtAddressLine1.setText(officeToEdit.getAddressLine1());
                txtAddressLine2.setText(officeToEdit.getAddressLine2() != null ? officeToEdit.getAddressLine2() : "");
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
            gbc.weightx = 0.40;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.60;
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(field, gbc);
        }

        private void onSave() {
            try {
                String code = txtOfficeCode.getText().trim();
                String city = txtCity.getText().trim();
                String country = txtCountry.getText().trim();
                String postalCode = txtPostalCode.getText().trim();
                String phone = txtPhone.getText().trim();
                String address1 = txtAddressLine1.getText().trim();

                if (code.isEmpty() || city.isEmpty() || country.isEmpty() ||
                        postalCode.isEmpty() || phone.isEmpty() || address1.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields marked with *.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (code.length() > 10) {
                    JOptionPane.showMessageDialog(this,
                            "Office Code cannot exceed 10 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (postalCode.length() > 10) {
                    JOptionPane.showMessageDialog(this,
                            "Postal Code cannot exceed 10 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (phone.length() > 20) {
                    JOptionPane.showMessageDialog(this,
                            "Phone cannot exceed 20 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Office office = new Office(
                        code,
                        city,
                        country,
                        txtRegion.getText().trim(),
                        postalCode,
                        phone,
                        address1,
                        txtAddressLine2.getText().trim()
                );

                if (officeToEdit == null) {
                    officeUseCase.save(office);
                    JOptionPane.showMessageDialog(this,
                            "Office created successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    officeUseCase.update(office);
                    JOptionPane.showMessageDialog(this,
                            "Office updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, officeToEdit == null ? "save" : "update", "Office");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
