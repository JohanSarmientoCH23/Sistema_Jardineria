package com.jardineria.infrastructure.adapters.in.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class VerdeVivoDashboardController {

    // Controles superiores
    @FXML private Label lblCurrentDate;
    @FXML private TextField txtSearch;
    @FXML private Button btnNewSale;

    // Gráfico de Ventas
    @FXML private BarChart<String, Number> salesBarChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // Tabla de Stock Bajo
    @FXML private TableView<LowStockItem> lowStockTable;
    @FXML private TableColumn<LowStockItem, String> colLowStockProduct;
    @FXML private TableColumn<LowStockItem, String> colLowStockUnits;

    // Tabla de Pedidos Recientes
    @FXML private TableView<RecentOrder> recentOrdersTable;
    @FXML private TableColumn<RecentOrder, String> colOrderCustomer;
    @FXML private TableColumn<RecentOrder, String> colOrderProduct;
    @FXML private TableColumn<RecentOrder, String> colOrderTotal;
    @FXML private TableColumn<RecentOrder, String> colOrderStatus;

    @FXML
    public void initialize() {
        initCurrentDate();
        initSalesChart();
        initLowStockTable();
        initRecentOrdersTable();
    }

    private void initCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", new Locale("es", "ES"));
        String dateStr = LocalDate.now().format(formatter);
        lblCurrentDate.setText(dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1));
    }

    private void initSalesChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Lun", 32));
        series.getData().add(new XYChart.Data<>("Mar", 40));
        series.getData().add(new XYChart.Data<>("Mié", 25));
        series.getData().add(new XYChart.Data<>("Jue", 38));
        series.getData().add(new XYChart.Data<>("Vie", 50));
        series.getData().add(new XYChart.Data<>("Sáb", 65)); // Pico sábado
        series.getData().add(new XYChart.Data<>("Dom", 56));

        salesBarChart.getData().add(series);

        salesBarChart.layout();
        for (XYChart.Data<String, Number> data : series.getData()) {
            if ("Sáb".equals(data.getXValue()) && data.getNode() != null) {
                data.getNode().getStyleClass().add("bar-peak");
            }
        }
    }

    private void initLowStockTable() {
        colLowStockProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colLowStockUnits.setCellValueFactory(new PropertyValueFactory<>("units"));

        colLowStockUnits.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    getStyleClass().add("low-stock-cell");
                }
            }
        });

        ObservableList<LowStockItem> lowStockData = FXCollections.observableArrayList(
                new LowStockItem("Monstera deliciosa", "2 uds"),
                new LowStockItem("Lavanda", "4 uds"),
                new LowStockItem("Tierra abonada 10 kg", "6 uds"),
                new LowStockItem("Maceta barro n.º 3", "8 uds"),
                new LowStockItem("Helecho Boston", "9 uds")
        );
        lowStockTable.setItems(lowStockData);
    }

    private void initRecentOrdersTable() {
        colOrderCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colOrderProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        colOrderTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colOrderStatus.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    badge.setText(status);
                    badge.getStyleClass().setAll("status-badge");

                    switch (status.trim().toLowerCase()) {
                        case "entregado" -> badge.getStyleClass().add("badge-delivered");
                        case "pendiente" -> badge.getStyleClass().add("badge-pending");
                        case "en camino" -> badge.getStyleClass().add("badge-transit");
                        default -> badge.getStyleClass().add("badge-default");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        ObservableList<RecentOrder> orders = FXCollections.observableArrayList(
                new RecentOrder("Laura Gómez", "Ficus y maceta", "$85.000", "Entregado"),
                new RecentOrder("Carlos Pérez", "Kit de huerta", "$120.000", "Pendiente"),
                new RecentOrder("Ana Rojas", "Orquídeas x3", "$210.000", "En camino")
        );
        recentOrdersTable.setItems(orders);
    }

    @FXML
    private void handleNewSale() {
        System.out.println("Acción: Nueva venta");
    }

    public static class LowStockItem {
        private final String productName;
        private final String units;

        public LowStockItem(String productName, String units) {
            this.productName = productName;
            this.units = units;
        }

        public String getProductName() { return productName; }
        public String getUnits() { return units; }
    }

    public static class RecentOrder {
        private final String customer;
        private final String product;
        private final String total;
        private final String status;

        public RecentOrder(String customer, String product, String total, String status) {
            this.customer = customer;
            this.product = product;
            this.total = total;
            this.status = status;
        }

        public String getCustomer() { return customer; }
        public String getProduct() { return product; }
        public String getTotal() { return total; }
        public String getStatus() { return status; }
    }
}