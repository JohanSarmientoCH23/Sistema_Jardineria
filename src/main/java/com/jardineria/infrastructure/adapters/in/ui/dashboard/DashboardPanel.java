package com.jardineria.infrastructure.adapters.in.ui.dashboard;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardPanel extends JPanel {

    // Paleta Orgánica Verde Vivo
    private static final Color BG_CANVAS = new Color(245, 246, 248);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(235, 238, 242);
    private static final Color BRAND_GREEN_DARK = new Color(39, 80, 10);     // #27500A
    private static final Color BRAND_GREEN_ACCENT = new Color(59, 109, 17);  // #3B6D11
    private static final Color ALERT_BG = new Color(252, 232, 195);          // Melocotón suave #FCE8C3
    private static final Color ALERT_BORDER = new Color(247, 212, 157);

    public DashboardPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 18));
        setBackground(BG_CANVAS);
        setBorder(new EmptyBorder(22, 28, 26, 28));

        // 1. Barra Superior (Header)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Contenedor principal scrolleable
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // 2. Fila de 4 Tarjetas KPIs
        mainContent.add(createKpiCardsPanel());
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // 3. Sección Media: Gráfico de Ventas + Tabla Stock Bajo
        mainContent.add(createMiddleSection());
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // 4. Sección Inferior: Pedidos Recientes
        mainContent.add(createRecentOrdersCard());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ==========================================
    // 1. BARRA SUPERIOR (Título, Fecha, Búsqueda, Botón)
    // ==========================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("Resumen del día");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(26, 32, 44));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", new Locale("es", "ES"));
        String dateStr = LocalDate.now().format(fmt);
        JLabel date = new JLabel(dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        date.setForeground(new Color(100, 116, 139));

        titleBox.add(title);
        titleBox.add(Box.createRigidArea(new Dimension(0, 2)));
        titleBox.add(date);

        // Controles de la derecha
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBox.setOpaque(false);

        JTextField txtSearch = new JTextField(16);
        txtSearch.setPreferredSize(new Dimension(210, 34));
        txtSearch.putClientProperty("JTextField.placeholderText", "Buscar planta o cliente");
        txtSearch.putClientProperty("JTextField.showClearButton", true);
        txtSearch.putClientProperty("Component.arc", 8);

        JButton btnNewSale = new JButton("Nueva venta");
        btnNewSale.setPreferredSize(new Dimension(125, 34));
        btnNewSale.setBackground(BRAND_GREEN_ACCENT);
        btnNewSale.setForeground(Color.WHITE);
        btnNewSale.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNewSale.setFocusPainted(false);
        btnNewSale.putClientProperty("JButton.buttonType", "roundRect");
        btnNewSale.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightBox.add(txtSearch);
        rightBox.add(btnNewSale);

        header.add(titleBox, BorderLayout.WEST);
        header.add(rightBox, BorderLayout.EAST);
        return header;
    }

    // ==========================================
    // 2. TARJETAS KPI (4 Tarjetas)
    // ==========================================
    private JPanel createKpiCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 16, 0));
        panel.setOpaque(false);

        panel.add(createKpiCard("Ventas de hoy", "$1.240.000", "+12% vs ayer", new Color(22, 163, 74), false));
        panel.add(createKpiCard("Pedidos", "38", "+5 nuevos", new Color(22, 163, 74), false));
        panel.add(createKpiCard("Plantas en stock", "1.862", "96 especies", new Color(100, 116, 139), false));
        panel.add(createKpiCard("Stock bajo", "14", "Requieren pedido", new Color(146, 64, 14), true));

        return panel;
    }

    private JPanel createKpiCard(String subtitle, String value, String footer, Color footerColor, boolean isAlert) {
        RoundedPanel card = new RoundedPanel(14, isAlert ? ALERT_BG : CARD_BG, isAlert ? ALERT_BORDER : CARD_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", isAlert ? Font.BOLD : Font.PLAIN, 12));
        lblSub.setForeground(isAlert ? new Color(133, 77, 14) : new Color(100, 116, 139));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(isAlert ? new Color(180, 83, 9) : new Color(15, 23, 42));

        JLabel lblFoot = new JLabel(footer);
        lblFoot.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFoot.setForeground(footerColor);

        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblVal);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblFoot);
        return card;
    }

    // ==========================================
    // 3. SECCIÓN MEDIA: Gráfico de Ventas + Stock Bajo
    // ==========================================
    private JPanel createMiddleSection() {
        JPanel section = new JPanel(new BorderLayout(16, 0));
        section.setOpaque(false);

        // Tarjeta Gráfico de Barras Izquierda
        RoundedPanel chartCard = new RoundedPanel(14, CARD_BG, CARD_BORDER);
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel chartTitle = new JLabel("Ventas de la semana");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartTitle.setForeground(new Color(30, 41, 59));

        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(createBarChartPanel(), BorderLayout.CENTER);

        // Tarjeta Tabla Stock Bajo Derecha
        RoundedPanel stockCard = new RoundedPanel(14, CARD_BG, CARD_BORDER);
        stockCard.setLayout(new BorderLayout());
        stockCard.setPreferredSize(new Dimension(340, 220));
        stockCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel stockTitle = new JLabel("Stock bajo");
        stockTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stockTitle.setForeground(new Color(30, 41, 59));

        stockCard.add(stockTitle, BorderLayout.NORTH);
        stockCard.add(createLowStockTable(), BorderLayout.CENTER);

        section.add(chartCard, BorderLayout.CENTER);
        section.add(stockCard, BorderLayout.EAST);
        return section;
    }

    private ChartPanel createBarChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(32, "Ventas", "Lun");
        dataset.addValue(40, "Ventas", "Mar");
        dataset.addValue(25, "Ventas", "Mié");
        dataset.addValue(38, "Ventas", "Jue");
        dataset.addValue(50, "Ventas", "Vie");
        dataset.addValue(65, "Ventas", "Sáb"); // Pico más alto
        dataset.addValue(56, "Ventas", "Dom");

        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(CARD_BG);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(CARD_BG);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                // Sábado en verde oscuro (#27500A), otros días en verde orgánico (#85BD45)
                return column == 5 ? BRAND_GREEN_DARK : new Color(133, 189, 69);
            }
        };
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        plot.setRenderer(renderer);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        domainAxis.setTickLabelPaint(new Color(100, 116, 139));
        domainAxis.setAxisLineVisible(true);
        domainAxis.setAxisLinePaint(new Color(226, 232, 240));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setVisible(false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 180));
        chartPanel.setOpaque(false);
        return chartPanel;
    }

    private JScrollPane createLowStockTable() {
        String[] columns = {"Producto", "Cantidad"};
        Object[][] data = {
                {"Monstera deliciosa", "2 uds"},
                {"Lavanda", "4 uds"},
                {"Tierra abonada 10 kg", "6 uds"},
                {"Maceta barro n.º 3", "8 uds"},
                {"Helecho Boston", "9 uds"}
        };

        JTable table = new JTable(new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setTableHeader(null); // Sin encabezado como en la imagen de referencia
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.RIGHT);
                setForeground(new Color(220, 38, 38)); // Cantidades en rojo
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return comp;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        sp.getViewport().setBackground(CARD_BG);
        return sp;
    }

    // ==========================================
    // 4. SECCIÓN INFERIOR: Pedidos Recientes con Badges
    // ==========================================
    private JPanel createRecentOrdersCard() {
        RoundedPanel card = new RoundedPanel(14, CARD_BG, CARD_BORDER);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("Pedidos recientes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 41, 59));
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Cliente", "Producto", "Total", "Estado"};
        Object[][] data = {
                {"Laura Gómez", "Ficus y maceta", "$85.000", "Entregado"},
                {"Carlos Pérez", "Kit de huerta", "$120.000", "Pendiente"},
                {"Ana Rojas", "Orquídeas x3", "$210.000", "En camino"}
        };

        JTable table = new JTable(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        table.setRowHeight(36);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setForeground(new Color(100, 116, 139));
        table.getTableHeader().setBackground(CARD_BG);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isS, boolean hasF, int r, int c) {
                String status = val != null ? val.toString() : "";
                JLabel badge = new JLabel(status, JLabel.CENTER);
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                badge.setBorder(new EmptyBorder(3, 10, 3, 10));

                switch (status.trim()) {
                    case "Entregado" -> {
                        badge.setBackground(new Color(220, 252, 231)); // Verde claro
                        badge.setForeground(new Color(22, 101, 52));
                    }
                    case "Pendiente" -> {
                        badge.setBackground(new Color(254, 243, 199)); // Ámbar claro
                        badge.setForeground(new Color(180, 83, 9));
                    }
                    case "En camino" -> {
                        badge.setBackground(new Color(219, 234, 254)); // Azul claro
                        badge.setForeground(new Color(30, 64, 175));
                    }
                }
                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
                wrapper.setBackground(CARD_BG);
                wrapper.add(badge);
                return wrapper;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        sp.getViewport().setBackground(CARD_BG);
        sp.setPreferredSize(new Dimension(0, 140));

        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ==========================================
    // Tarjeta con Bordes Suavizados (Radio 14px)
    // ==========================================
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color border;

        public RoundedPanel(int radius, Color bg, Color border) {
            this.radius = radius;
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(border);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}