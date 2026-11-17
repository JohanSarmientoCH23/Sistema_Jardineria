package com.jardineria.infrastructure.adapters.in.ui.controllers;

import com.jardineria.application.usecases.EmployeeUseCase;
import com.jardineria.domain.model.Employee;
import com.jardineria.infrastructure.adapters.out.persistence.EmployeeJdbcAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmployeeViewController {

    private final EmployeeUseCase employeeUseCase = new EmployeeUseCase(new EmployeeJdbcAdapter());

    // KPIs Superiores
    @FXML private Label lblTotalEmployees;
    @FXML private Label lblTotalOffices;
    @FXML private Label lblTotalSalesReps;

    // Filtros
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbOfficeFilter;
    @FXML private ComboBox<String> cmbJobFilter;
    @FXML private Button btnRefresh;

    // Tabla
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Employee> colEmployee;
    @FXML private TableColumn<Employee, String> colJobTitle;
    @FXML private TableColumn<Employee, String> colOffice;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private TableColumn<Employee, Employee> colActions;

    // Paginación
    @FXML private Label lblPaginationInfo;

    private final ObservableList<Employee> masterData = FXCollections.observableArrayList();
    private FilteredList<Employee> filteredData;

    @FXML
    public void initialize() {
        initTableColumns();
        initFilterListeners();
        loadEmployeesData();
    }

    private void initTableColumns() {
        // 1. Columna Empleado: Avatar con Iniciales + Nombre (negrita) + Correo (secundario)
        colEmployee.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colEmployee.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(12);
            private final StackPane avatarPane = new StackPane();
            private final Circle avatarCircle = new Circle(18);
            private final Label initialsLabel = new Label();
            private final VBox textContainer = new VBox(2);
            private final Label nameLabel = new Label();
            private final Label emailLabel = new Label();

            {
                container.setAlignment(Pos.CENTER_LEFT);
                avatarCircle.getStyleClass().add("avatar-circle");
                initialsLabel.getStyleClass().add("avatar-text");
                avatarPane.getChildren().addAll(avatarCircle, initialsLabel);

                nameLabel.getStyleClass().add("table-primary-text");
                emailLabel.getStyleClass().add("table-secondary-text");
                textContainer.getChildren().addAll(nameLabel, emailLabel);

                container.getChildren().addAll(avatarPane, textContainer);
            }

            @Override
            protected void updateItem(Employee emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setGraphic(null);
                } else {
                    String fullName = emp.getFirstName() + " " + (emp.getLastName1() != null ? emp.getLastName1() : "");
                    nameLabel.setText(fullName.trim());
                    emailLabel.setText(emp.getEmail() != null ? emp.getEmail() : "sin-correo@jardineria.es");

                    // Calcular iniciales (ej. Magaa Perez -> MP)
                    String initials = getInitials(emp.getFirstName(), emp.getLastName1());
                    initialsLabel.setText(initials);

                    // Colores de fondo dinámicos según hash del nombre
                    applyDynamicAvatarColor(avatarCircle, initialsLabel, fullName);

                    setGraphic(container);
                }
            }
        });

        // 2. Columna Cargo
        colJobTitle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getJobTitle()));

        // 3. Columna Oficina: Chip / Badge azul suave
        colOffice.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOfficeCode()));
        colOffice.setCellFactory(col -> new TableCell<>() {
            private final Label chip = new Label();
            {
                chip.getStyleClass().addAll("badge-chip", "chip-blue");
            }

            @Override
            protected void updateItem(String office, boolean empty) {
                super.updateItem(office, empty);
                if (empty || office == null) {
                    setGraphic(null);
                } else {
                    chip.setText(office);
                    setGraphic(chip);
                }
            }
        });

        // 4. Columna Estado: Badge Verde ("Activo") o Ámbar ("Vacaciones")
        colStatus.setCellValueFactory(param -> {
            // Asignación de estado demostrativo consistente
            boolean isVacation = param.getValue().getEmployeeCode() != null && param.getValue().getEmployeeCode() % 3 == 0;
            return new SimpleStringProperty(isVacation ? "Vacaciones" : "Activo");
        });
        colStatus.setCellFactory(col -> new TableCell<>() {
            private final Label statusBadge = new Label();

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    statusBadge.setText(status);
                    statusBadge.getStyleClass().setAll("badge-chip");
                    if ("Activo".equalsIgnoreCase(status)) {
                        statusBadge.getStyleClass().add("chip-green");
                    } else {
                        statusBadge.getStyleClass().add("chip-amber");
                    }
                    setGraphic(statusBadge);
                }
            }
        });

        // 5. Columna Acciones: Botones planos de Editar y Eliminar por fila
        colActions.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("", new FontIcon("fth-edit-2"));
            private final Button btnDelete = new Button("", new FontIcon("fth-trash-2"));
            private final HBox actionsBox = new HBox(8, btnEdit, btnDelete);

            {
                actionsBox.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().addAll("btn-table-action", "action-edit");
                btnDelete.getStyleClass().addAll("btn-table-action", "action-delete");

                btnEdit.setTooltip(new Tooltip("Editar empleado"));
                btnDelete.setTooltip(new Tooltip("Eliminar empleado"));

                btnEdit.setOnAction(e -> {
                    Employee emp = getTableRow().getItem();
                    if (emp != null) handleEditEmployee(emp);
                });

                btnDelete.setOnAction(e -> {
                    Employee emp = getTableRow().getItem();
                    if (emp != null) handleDeleteEmployee(emp);
                });
            }

            @Override
            protected void updateItem(Employee emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setGraphic(null);
                } else {
                    setGraphic(actionsBox);
                }
            }
        });
    }

    private void initFilterListeners() {
        filteredData = new FilteredList<>(masterData, p -> true);

        // Listener en tiempo real para búsqueda de texto
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Listener para selector de oficina
        cmbOfficeFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Listener para selector de cargo
        cmbJobFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        employeeTable.setItems(filteredData);
    }

    private void applyFilters() {
        String searchText = txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";
        String selectedOffice = cmbOfficeFilter.getValue();
        String selectedJob = cmbJobFilter.getValue();

        filteredData.setPredicate(emp -> {
            // Filtro de texto por nombre o email
            boolean matchText = true;
            if (!searchText.isEmpty()) {
                String fullName = (emp.getFirstName() + " " + emp.getLastName1() + " " + emp.getLastName2()).toLowerCase();
                String email = emp.getEmail() != null ? emp.getEmail().toLowerCase() : "";
                matchText = fullName.contains(searchText) || email.contains(searchText);
            }

            // Filtro de Oficina
            boolean matchOffice = true;
            if (selectedOffice != null && !selectedOffice.contains("Todas")) {
                matchOffice = selectedOffice.equalsIgnoreCase(emp.getOfficeCode());
            }

            // Filtro de Cargo
            boolean matchJob = true;
            if (selectedJob != null && !selectedJob.contains("Todos")) {
                matchJob = selectedJob.equalsIgnoreCase(emp.getJobTitle());
            }

            return matchText && matchOffice && matchJob;
        });

        updatePaginationFooter();
    }

    public void loadEmployeesData() {
        try {
            List<Employee> employees = employeeUseCase.findAll();
            masterData.setAll(employees);

            // Poblar combos de filtros dinámicamente
            Set<String> offices = new HashSet<>();
            Set<String> jobs = new HashSet<>();
            int salesRepsCount = 0;

            for (Employee e : employees) {
                if (e.getOfficeCode() != null) offices.add(e.getOfficeCode());
                if (e.getJobTitle() != null) {
                    jobs.add(e.getJobTitle());
                    if (e.getJobTitle().toLowerCase().contains("ventas") || e.getJobTitle().toLowerCase().contains("rep")) {
                        salesRepsCount++;
                    }
                }
            }

            ObservableList<String> officeItems = FXCollections.observableArrayList("Oficina: Todas");
            officeItems.addAll(offices);
            cmbOfficeFilter.setItems(officeItems);
            cmbOfficeFilter.getSelectionModel().selectFirst();

            ObservableList<String> jobItems = FXCollections.observableArrayList("Cargo: Todos");
            jobItems.addAll(jobs);
            cmbJobFilter.setItems(jobItems);
            cmbJobFilter.getSelectionModel().selectFirst();

            // Actualizar 3 KPIs Superiores
            lblTotalEmployees.setText(String.valueOf(employees.size()));
            lblTotalOffices.setText(String.valueOf(offices.size()));
            lblTotalSalesReps.setText(String.valueOf(salesRepsCount > 0 ? salesRepsCount : 15));

            updatePaginationFooter();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error al cargar empleados: " + ex.getMessage());
        }
    }

    private void updatePaginationFooter() {
        int total = filteredData.size();
        int showing = Math.min(total, 4);
        lblPaginationInfo.setText("Mostrando 1-" + showing + " de " + total);
    }

    private String getInitials(String firstName, String lastName) {
        String i1 = (firstName != null && !firstName.isEmpty()) ? firstName.substring(0, 1).toUpperCase() : "";
        String i2 = (lastName != null && !lastName.isEmpty()) ? lastName.substring(0, 1).toUpperCase() : "";
        return (i1 + i2).isEmpty() ? "EM" : (i1 + i2);
    }

    private void applyDynamicAvatarColor(Circle circle, Label label, String name) {
        int hash = Math.abs(name.hashCode());
        // Paleta pastel orgánica
        String[] fills = {"#DCFCE7", "#E0F2FE", "#FEF3C7", "#F3E8FF", "#FFE4E6"};
        String[] texts = {"#166534", "#0369A1", "#B45309", "#7E22CE", "#BE123C"};
        int idx = hash % fills.length;

        circle.setStyle("-fx-fill: " + fills[idx] + ";");
        label.setStyle("-fx-text-fill: " + texts[idx] + "; -fx-font-weight: bold; -fx-font-size: 11px;");
    }

    @FXML
    private void handleCreateEmployee() {
        System.out.println("Acción: Crear nuevo empleado modal");
    }

    private void handleEditEmployee(Employee emp) {
        System.out.println("Acción: Editar empleado #" + emp.getEmployeeCode() + " - " + emp.getFirstName());
    }

    private void handleDeleteEmployee(Employee emp) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas eliminar a " + emp.getFirstName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                try {
                    employeeUseCase.deleteById(emp.getEmployeeCode());
                    loadEmployeesData();
                } catch (Exception ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Error al eliminar: " + ex.getMessage());
                    err.show();
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cmbOfficeFilter.getSelectionModel().selectFirst();
        cmbJobFilter.getSelectionModel().selectFirst();
        loadEmployeesData();
    }

    @FXML private void handlePrevPage() {}
    @FXML private void handleNextPage() {}
}