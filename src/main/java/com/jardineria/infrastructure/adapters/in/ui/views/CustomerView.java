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
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.util.regex.Pattern;

public class CustomerView extends JPanel {

    // Paleta corporativa moderna sincronizada con el Dashboard
    private static final Color BG_CANVAS = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(226, 232, 240);
    private static final Color TEXT_PRIMARY = new Color(24, 38, 57);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color COLOR_PRIMARY_GREEN = new Color(40, 167, 69);
    private static final Color COLOR_DANGER = new Color(220, 38, 38);
    private static final Color BORDER_SUBTLE = new Color(209, 213, 219);

    private final CustomerUseCase customerUseCase = new CustomerUseCase(new CustomerJdbcAdapter());
    private final EmployeeUseCase employeeUseCase = new EmployeeUseCase(new EmployeeJdbcAdapter());

    private DefaultTableModel tableModel;
    private JTable customerTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtSearch;

    public CustomerView() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 18));
        setBackground(BG_CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 1. Panel Superior: Encabezados y Barra de Acciones (Búsqueda + CRUD)
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 2. Panel Central: Tarjeta contenedora con JTable estilizado
        JPanel tableCard = createTableCard();
        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 14));
        container.setOpaque(false);

        // Títulos
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Real-time list of clients, contacts & credit limits");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        titlePanel.add(subtitleLabel);

        container.add(titlePanel, BorderLayout.NORTH);

        // Barra de Herramientas
        JPanel toolbarPanel = new JPanel(new BorderLayout(15, 0));
        toolbarPanel.setOpaque(false);

        // Izquierda: Campo de Búsqueda Dinámica con filtro en tiempo real
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(24);
        txtSearch.setPreferredSize(new Dimension(280, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.putClientProperty("JTextField.placeholderText", "Search by code, name, or city...");
        txtSearch.putClientProperty("JTextField.showClearButton", true);
        txtSearch.putClientProperty("Component.arc", 8);

        // Filtrado reactivo en tiempo real mientras el usuario escribe
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }

            private void applyFilter() {
                if (rowSorter == null) return;
                String query = txtSearch.getText().trim();
                if (query.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
                }
            }
        });

        JButton btnFilter = new JButton("Filter");
        btnFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnFilter.setPreferredSize(new Dimension(75, 36));
        btnFilter.setBackground(CARD_BG);
        btnFilter.setForeground(TEXT_PRIMARY);
        btnFilter.setFocusPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.putClientProperty("JButton.buttonType", "roundRect");
        btnFilter.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1, true));
        btnFilter.addActionListener(e -> txtSearch.requestFocusInWindow());

        searchPanel.add(txtSearch);
        searchPanel.add(btnFilter);

        // Derecha: Botones Modernos CRUD
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnAdd = createActionButton("+ Add Customer", COLOR_PRIMARY_GREEN, Color.WHITE, true, 135);
        JButton btnEdit = createActionButton("Edit", CARD_BG, TEXT_PRIMARY, false, 80);
        JButton btnDelete = createActionButton("Delete", CARD_BG, COLOR_DANGER, false, 85);
        JButton btnRefresh = createActionButton("Refresh", CARD_BG, TEXT_PRIMARY, false, 90);

        btnAdd.addActionListener(e -> showCustomerDialog(null));
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        toolbarPanel.add(searchPanel, BorderLayout.WEST);
        toolbarPanel.add(buttonPanel, BorderLayout.EAST);

        container.add(toolbarPanel, BorderLayout.SOUTH);
        return container;
    }

    private JButton createActionButton(String text, Color bg, Color fg, boolean isPrimary, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(width, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");

        if (isPrimary) {
            btn.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        } else {
            btn.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1, true));
        }

        return btn;
    }

    private JPanel createTableCard() {
        RoundedCardPanel card = new RoundedCardPanel(12);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Color alterno en filas mediante FlatLaf
        UIManager.put("Table.alternateRowColor", new Color(245, 248, 250));

        String[] columns = {"Code", "Name", "Contact", "Phone", "City", "Country", "Credit Limit"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setRowHeight(35); // Altura generosa para legibilidad
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setShowVerticalLines(false); // Sin líneas verticales
        customerTable.setShowHorizontalLines(true);
        customerTable.setGridColor(new Color(238, 242, 246));

        // Encabezado en negrita y sin reordenamiento
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.getTableHeader().setReorderingAllowed(false);
        customerTable.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // TableRowSorter para ordenamiento y filtrado reactivo
        rowSorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(rowSorter);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        customerTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        customerTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        customerTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        customerTable.getColumnModel().getColumn(6).setPreferredWidth(130);

        // Resaltado de Credit Limit en verde con alineación derecha
        customerTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 14));
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
                        c.getCreditLimit() != null ? String.format("$%,.2f", c.getCreditLimit()) : "$0.00"
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

        // Conversión de índice de vista a modelo para compatibilidad con filtros activos
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
    // Diálogo Modal Corporativo (Add / Edit)
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
            setSize(540, 600);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            setLayout(new BorderLayout());

            // Banner superior del diálogo
            JPanel headerBanner = new JPanel(new BorderLayout());
            headerBanner.setBackground(new Color(24, 38, 57));
            headerBanner.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

            JLabel title = new JLabel(customerToEdit == null ? "Add New Customer Account" : "Update Customer Record");
            title.setFont(new Font("Segoe UI", Font.BOLD, 17));
            title.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel(customerToEdit == null ? "Client ID is auto-generated. Fill in company details & credit limit" : "Modify existing client parameters and address lines");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(203, 213, 225));

            headerBanner.add(title, BorderLayout.NORTH);
            headerBanner.add(subtitle, BorderLayout.SOUTH);
            add(headerBanner, BorderLayout.NORTH);

            // Formulario
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
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
            cmbSalesRep.putClientProperty("Component.arc", 8);
            loadSalesReps();
            txtCreditLimit = createStyledTextField();
            txtCreditLimit.setText("0.00");

            // Configuración del campo de código: siempre bloqueado; "Auto-generated" al agregar o el número al editar
            txtCode.setEnabled(false);
            if (customerToEdit == null) {
                txtCode.setText("Auto-generated");
                txtCode.setForeground(new Color(100, 116, 139));
            } else {
                txtCode.setText(String.valueOf(customerToEdit.getCustomerCode()));
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

            int row = 0;
            addFormField(formPanel, gbc, "Customer Code:", txtCode, row++);
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

            add(formPanel, BorderLayout.CENTER);

            // Botones inferiores de acción
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
            buttonPanel.setBackground(new Color(248, 250, 252));
            buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnCancel.setPreferredSize(new Dimension(85, 34));
            btnCancel.setBackground(Color.WHITE);
            btnCancel.setForeground(TEXT_PRIMARY);
            btnCancel.setFocusPainted(false);
            btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCancel.putClientProperty("JButton.buttonType", "roundRect");
            btnCancel.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1, true));

            JButton btnSave = new JButton("Save Record");
            btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnSave.setPreferredSize(new Dimension(115, 34));
            btnSave.setBackground(new Color(24, 38, 57));
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnSave.putClientProperty("JButton.buttonType", "roundRect");
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
            tf.putClientProperty("Component.arc", 8);
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
                String name = txtName.getText().trim();
                String phone = txtPhone.getText().trim();
                String address = txtAddressLine1.getText().trim();
                String city = txtCity.getText().trim();
                String country = txtCountry.getText().trim();

                // Validación de campos requeridos (sin exigir Code para nuevo registro)
                if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || country.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in all required fields (Company Name, Phone, Address, City, Country).",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
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

                // Si es nuevo registro, el código inicial es 0 (CustomerJdbcAdapter autogenera el código real secuencial)
                int code = (customerToEdit != null) ? customerToEdit.getCustomerCode() : 0;

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
                            "Customer #" + customer.getCustomerCode() + " saved successfully.",
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

    private static class RoundedCardPanel extends JPanel {
        private final int radius;

        public RoundedCardPanel(int radius) {
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
