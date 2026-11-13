package com.jardineria.infrastructure.adapters.in.ui.views;

import com.jardineria.application.usecases.EmployeeUseCase;
import com.jardineria.application.usecases.OfficeUseCase;
import com.jardineria.domain.model.Employee;
import com.jardineria.domain.model.Office;
import com.jardineria.infrastructure.adapters.out.persistence.EmployeeJdbcAdapter;
import com.jardineria.infrastructure.adapters.out.persistence.OfficeJdbcAdapter;

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
import java.util.List;

public class EmployeeView extends JPanel {

    private final EmployeeUseCase employeeUseCase = new EmployeeUseCase(new EmployeeJdbcAdapter());
    private final OfficeUseCase officeUseCase = new OfficeUseCase(new OfficeJdbcAdapter());
    private DefaultTableModel tableModel;
    private JTable employeeTable;

    public EmployeeView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Top Panel: Title and Action Buttons (Add, Edit, Delete, Refresh)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Employee Management");
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

        btnAdd.addActionListener(e -> showEmployeeDialog(null));
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
        String[] columns = {"Code", "First Name", "Last Name", "Email", "Job Title", "Office Code"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(28);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        employeeTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Employee> employees = employeeUseCase.findAll();
            for (Employee emp : employees) {
                String lastName = (emp.getLastName1() != null ? emp.getLastName1() : "") +
                        (emp.getLastName2() != null && !emp.getLastName2().isBlank() ? " " + emp.getLastName2() : "");

                tableModel.addRow(new Object[]{
                        emp.getEmployeeCode(),
                        emp.getFirstName(),
                        lastName.trim(),
                        emp.getEmail(),
                        emp.getJobTitle(),
                        emp.getOfficeCode()
                });
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Database Error", ex, "read", "Employees");
        }
    }

    private void handleEdit() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee from the table to edit.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        Integer employeeCode = (Integer) tableModel.getValueAt(modelRow, 0);

        employeeUseCase.findById(employeeCode).ifPresentOrElse(
                this::showEmployeeDialog,
                () -> JOptionPane.showMessageDialog(this,
                        "Selected employee not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void handleDelete() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        Integer employeeCode = (Integer) tableModel.getValueAt(modelRow, 0);
        String employeeName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete employee #" + employeeCode + " (" + employeeName + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeUseCase.deleteById(employeeCode);
                JOptionPane.showMessageDialog(this,
                        "Employee deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, "delete", "Employee");
            }
        }
    }

    private void showEmployeeDialog(Employee employeeToEdit) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        EmployeeFormDialog dialog = new EmployeeFormDialog(owner, employeeToEdit);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    // Modal Form Dialog for Add / Edit
    private class EmployeeFormDialog extends JDialog {
        private final Employee employeeToEdit;
        private boolean saved = false;

        private JTextField txtCode;
        private JTextField txtFirstName;
        private JTextField txtLastName1;
        private JTextField txtLastName2;
        private JTextField txtExtension;
        private JTextField txtEmail;
        private JComboBox<String> cmbOfficeCode;
        private JComboBox<String> cmbReportsTo;
        private JTextField txtJobTitle;

        public EmployeeFormDialog(Frame owner, Employee employeeToEdit) {
            super(owner, employeeToEdit == null ? "Add Employee" : "Edit Employee #" + employeeToEdit.getEmployeeCode(), true);
            this.employeeToEdit = employeeToEdit;
            initDialog();
        }

        private void initDialog() {
            setSize(510, 530);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout(15, 15));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtCode = new JTextField(15);
            txtFirstName = new JTextField(15);
            txtLastName1 = new JTextField(15);
            txtLastName2 = new JTextField(15);
            txtExtension = new JTextField(15);
            txtEmail = new JTextField(15);
            cmbOfficeCode = new JComboBox<>();
            loadOffices();

            cmbReportsTo = new JComboBox<>();
            loadManagers();

            txtJobTitle = new JTextField(15);

            int row = 0;
            addFormField(formPanel, gbc, "Employee Code *:", txtCode, row++);
            addFormField(formPanel, gbc, "First Name *:", txtFirstName, row++);
            addFormField(formPanel, gbc, "Last Name 1 *:", txtLastName1, row++);
            addFormField(formPanel, gbc, "Last Name 2:", txtLastName2, row++);
            addFormField(formPanel, gbc, "Extension *:", txtExtension, row++);
            addFormField(formPanel, gbc, "Email *:", txtEmail, row++);
            addFormCombo(formPanel, gbc, "Office *:", cmbOfficeCode, row++);
            addFormCombo(formPanel, gbc, "Reports To (Manager):", cmbReportsTo, row++);
            addFormField(formPanel, gbc, "Job Title *:", txtJobTitle, row++);

            if (employeeToEdit != null) {
                txtCode.setText(String.valueOf(employeeToEdit.getEmployeeCode()));
                txtCode.setEnabled(false);
                txtFirstName.setText(employeeToEdit.getFirstName());
                txtLastName1.setText(employeeToEdit.getLastName1());
                txtLastName2.setText(employeeToEdit.getLastName2() != null ? employeeToEdit.getLastName2() : "");
                txtExtension.setText(employeeToEdit.getExtension());
                txtEmail.setText(employeeToEdit.getEmail());

                // Select Office
                for (int i = 0; i < cmbOfficeCode.getItemCount(); i++) {
                    if (cmbOfficeCode.getItemAt(i).startsWith(employeeToEdit.getOfficeCode())) {
                        cmbOfficeCode.setSelectedIndex(i);
                        break;
                    }
                }

                // Select Manager
                if (employeeToEdit.getReportsTo() != null) {
                    for (int i = 0; i < cmbReportsTo.getItemCount(); i++) {
                        if (cmbReportsTo.getItemAt(i).startsWith(employeeToEdit.getReportsTo() + " -")) {
                            cmbReportsTo.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    cmbReportsTo.setSelectedIndex(0);
                }

                txtJobTitle.setText(employeeToEdit.getJobTitle());
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

        private void loadOffices() {
            try {
                List<Office> offices = officeUseCase.findAll();
                for (Office o : offices) {
                    cmbOfficeCode.addItem(o.getOfficeCode() + " (" + o.getCity() + ")");
                }
            } catch (Exception ignored) {
            }
            if (cmbOfficeCode.getItemCount() == 0) {
                cmbOfficeCode.setEditable(true);
            }
        }

        private void loadManagers() {
            cmbReportsTo.addItem("None (No Manager)");
            try {
                List<Employee> employees = employeeUseCase.findAll();
                for (Employee emp : employees) {
                    cmbReportsTo.addItem(emp.getEmployeeCode() + " - " + emp.getFirstName() + " " + emp.getLastName1());
                }
            } catch (Exception ignored) {
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
                String codeStr = txtCode.getText().trim();
                String firstName = txtFirstName.getText().trim();
                String lastName1 = txtLastName1.getText().trim();
                String extension = txtExtension.getText().trim();
                String email = txtEmail.getText().trim();
                String jobTitle = txtJobTitle.getText().trim();

                Object selectedOffice = cmbOfficeCode.getSelectedItem();
                if (codeStr.isEmpty() || firstName.isEmpty() || lastName1.isEmpty() ||
                        extension.isEmpty() || email.isEmpty() || selectedOffice == null || jobTitle.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields marked with *.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String officeStr = selectedOffice.toString().trim();
                String officeCode;
                if (officeStr.contains(" (")) {
                    officeCode = officeStr.substring(0, officeStr.indexOf(" (")).trim();
                } else {
                    officeCode = officeStr;
                }

                int code;
                try {
                    code = Integer.parseInt(codeStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Employee Code must be a valid integer number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer reportsTo = null;
                Object selectedMgr = cmbReportsTo.getSelectedItem();
                if (selectedMgr != null && !selectedMgr.toString().startsWith("None")) {
                    String mgrStr = selectedMgr.toString().trim();
                    reportsTo = Integer.parseInt(mgrStr.split(" - ")[0].trim());
                }

                if (extension.length() > 10) {
                    JOptionPane.showMessageDialog(this,
                            "Extension cannot exceed 10 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (email.length() > 100) {
                    JOptionPane.showMessageDialog(this,
                            "Email cannot exceed 100 characters.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Employee employee = new Employee(
                        code,
                        firstName,
                        lastName1,
                        txtLastName2.getText().trim(),
                        extension,
                        email,
                        officeCode,
                        reportsTo,
                        jobTitle
                );

                if (employeeToEdit == null) {
                    employeeUseCase.save(employee);
                    JOptionPane.showMessageDialog(this,
                            "Employee created successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    employeeUseCase.update(employee);
                    JOptionPane.showMessageDialog(this,
                            "Employee updated successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                saved = true;
                dispose();
            } catch (Exception ex) {
                UIUtils.showError(this, "Database Error", ex, employeeToEdit == null ? "save" : "update", "Employee");
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
