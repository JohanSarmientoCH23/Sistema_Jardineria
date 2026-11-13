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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;

public class DashboardPanel extends JPanel {

    // Paleta de Colores Corporativa
    private static final Color BG_CANVAS = new Color(245, 246, 248);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(230, 230, 235);
    private static final Color TEXT_PRIMARY = new Color(24, 38, 57);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color ACCENT_AMBER = new Color(245, 158, 11);

    public DashboardPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_CANVAS);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. Cabecera (Título, Subtítulo y Controles)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Contenedor principal con scroll vertical
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);

        // 2. Fila de 3 Tarjetas de KPIs
        JPanel kpiRow = createKpiRow();
        mainContent.add(kpiRow, BorderLayout.NORTH);

        // 3. Sección Dividida (Izquierda: Gráfico, Derecha: Clientes Top y Tareas)
        JPanel splitSection = createSplitSection();
        mainContent.add(splitSection, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("System Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Real-time enterprise overview, KPIs & product line analytics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        titleBox.add(titleLabel);
        titleBox.add(Box.createRigidArea(new Dimension(0, 3)));
        titleBox.add(subtitleLabel);

        // Controles de fecha y exportación
        JPanel controlsBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsBox.setOpaque(false);

        JComboBox<String> dateRangeCombo = new JComboBox<>(new String[]{
                "Last 30 Days", "This Quarter", "This Year", "All Time"
        });
        dateRangeCombo.setPreferredSize(new Dimension(135, 34));
        dateRangeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnExport = new JButton("Export PDF");
        btnExport.setPreferredSize(new Dimension(105, 34));
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(CARD_BG);
        btnExport.setForeground(TEXT_PRIMARY);
        btnExport.setFocusPainted(false);
        btnExport.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));

        controlsBox.add(dateRangeCombo);
        controlsBox.add(btnExport);

        header.add(titleBox, BorderLayout.WEST);
        header.add(controlsBox, BorderLayout.EAST);

        return header;
    }

    private JPanel createKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 18, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 125));

        // Card 1: Total Customers
        row.add(createKpiCard(
                "TOTAL CUSTOMERS",
                "36",
                "+8.5% vs last month",
                ACCENT_GREEN,
                new SparklineComponent(true),
                new CircleIconBadge(new UserBadgeIcon(18, 18), new Color(239, 246, 255), ACCENT_BLUE)
        ));

        // Card 2: Active Orders
        row.add(createKpiCard(
                "ACTIVE ORDERS",
                "47",
                "+12.4% este mes",
                ACCENT_GREEN,
                null,
                new CircleIconBadge(new CartBadgeIcon(18, 18), new Color(240, 253, 244), ACCENT_GREEN)
        ));

        // Card 3: Products in Catalog
        row.add(createKpiCard(
                "PRODUCTS IN CATALOG",
                "128",
                "5 sin stock",
                ACCENT_RED,
                null,
                new CircleIconBadge(new BoxBadgeIcon(18, 18), new Color(254, 243, 199), ACCENT_AMBER)
        ));

        return row;
    }

    private JPanel createKpiCard(String title, String mainValue, String badgeText, Color badgeColor,
                                 JComponent extraComponent, JComponent iconBadge) {
        RoundedCardPanel card = new RoundedCardPanel(14);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(mainValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblValue.setForeground(TEXT_PRIMARY);

        JPanel bottomBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        bottomBox.setOpaque(false);

        JLabel lblBadge = new JLabel(badgeText);
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(badgeColor);

        bottomBox.add(lblBadge);
        if (extraComponent != null) {
            bottomBox.add(extraComponent);
        }

        leftBox.add(lblTitle);
        leftBox.add(Box.createRigidArea(new Dimension(0, 4)));
        leftBox.add(lblValue);
        leftBox.add(Box.createRigidArea(new Dimension(0, 6)));
        leftBox.add(bottomBox);

        card.add(leftBox, BorderLayout.CENTER);
        if (iconBadge != null) {
            JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightWrapper.setOpaque(false);
            rightWrapper.add(iconBadge);
            card.add(rightWrapper, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel createSplitSection() {
        JPanel split = new JPanel(new GridBagLayout());
        split.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 24); // Mayor espaciado horizontal entre gráfico y panel derecho
        gbc.weighty = 1.0;

        // Columna Izquierda: Gráfico de Ventas
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.62;
        JPanel chartCard = createChartCard();
        split.add(chartCard, gbc);

        // Columna Derecha: Clientes Top y Tareas
        gbc.gridx = 1;
        gbc.weightx = 0.38;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel rightCol = createRightColumn();
        split.add(rightCol, gbc);

        return split;
    }

    private JPanel createChartCard() {
        RoundedCardPanel card = new RoundedCardPanel(14);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);

        JLabel chartTitle = new JLabel("Sales by Product Line");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartTitle.setForeground(TEXT_PRIMARY);

        JLabel chartSubtitle = new JLabel("Revenue breakdown across official gardening categories");
        chartSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chartSubtitle.setForeground(TEXT_SECONDARY);

        titles.add(chartTitle);
        titles.add(Box.createRigidArea(new Dimension(0, 2)));
        titles.add(chartSubtitle);

        cardHeader.add(titles, BorderLayout.WEST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Gráfico JFreeChart estilizado
        JFreeChart barChart = createStyledBarChart();
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(500, 320));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDisplayToolTips(true); // Tooltips nativos habilitados

        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    private JFreeChart createStyledBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(35200.00, "Sales", "Árboles");
        dataset.addValue(26800.00, "Sales", "Herramientas");
        dataset.addValue(21500.00, "Sales", "Frutales");
        dataset.addValue(16400.00, "Sales", "Herbáceas");
        dataset.addValue(12900.00, "Sales", "Aromáticas");

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                null,
                "Total Revenue (USD)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true, // Habilitar tooltips
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.setPadding(new org.jfree.chart.ui.RectangleInsets(10, 5, 5, 5));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(241, 245, 249));
        plot.setRangeGridlineStroke(new BasicStroke(1.0f));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        domainAxis.setTickLabelPaint(TEXT_SECONDARY);
        domainAxis.setAxisLineVisible(false);

        // Corrección Crítica del Eje Y (NumberAxis)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(0.0);                    // Empieza estrictamente en cero (sin negativos)
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0")); // Formato numérico limpio sin notación científica
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        rangeAxis.setTickLabelPaint(TEXT_SECONDARY);
        rangeAxis.setAxisLineVisible(false);

        // Renderizador Plano con Paleta Corporativa (Azul marino, Verde suave, Coral, etc.)
        BarRenderer renderer = new BarRenderer() {
            private final Paint[] colors = new Paint[]{
                    new Color(24, 38, 57),       // Azul marino
                    new Color(76, 175, 80),      // Verde suave
                    new Color(239, 107, 74),     // Coral
                    new Color(56, 118, 140),     // Acero
                    new Color(120, 144, 156)     // Gris pizarra
            };

            @Override
            public Paint getItemPaint(int row, int column) {
                return colors[column % colors.length];
            }
        };

        renderer.setBarPainter(new StandardBarPainter()); // Desactiva brillos/degradados 3D para estilo plano
        renderer.setShadowVisible(false);                 // Desactiva sombras toscas
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.12);

        // Tooltip nativo interactivo al pasar el mouse
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{1}: ${2}", new DecimalFormat("#,##0")
        ));

        plot.setRenderer(renderer);

        return chart;
    }

    private JPanel createRightColumn() {
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        // Tarjeta de Clientes Destacados
        JPanel topCustomersCard = createTopCustomersCard();
        rightCol.add(topCustomersCard);

        rightCol.add(Box.createRigidArea(new Dimension(0, 16)));

        // Tarjeta de Tareas y Alertas
        JPanel alertsCard = createAlertsCard();
        rightCol.add(alertsCard);

        return rightCol;
    }

    private JPanel createTopCustomersCard() {
        RoundedCardPanel card = new RoundedCardPanel(14);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel title = new JLabel("Top Customers");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_PRIMARY);

        JLabel viewAll = new JLabel("View All");
        viewAll.setFont(new Font("Segoe UI", Font.BOLD, 11));
        viewAll.setForeground(new Color(37, 99, 235));

        cardHeader.add(title, BorderLayout.WEST);
        cardHeader.add(viewAll, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        listPanel.add(createCustomerRow("GG", "GoldFish Garden", "ES", "$3,000", new Color(224, 231, 255), new Color(67, 56, 202)));
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        listPanel.add(createCustomerRow("GV", "Gerudo Valley", "USA", "$12,000", new Color(254, 243, 199), new Color(180, 83, 9)));
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        listPanel.add(createCustomerRow("LA", "Lasas S.A.", "ES", "$30,000", new Color(220, 252, 231), new Color(21, 128, 61)));
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        listPanel.add(createCustomerRow("TG", "Tendo Garden", "FR", "$15,000", new Color(243, 232, 255), new Color(126, 34, 206)));

        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCustomerRow(String initials, String name, String country, String creditLimit, Color avatarBg, Color avatarFg) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        left.setOpaque(false);

        JLabel avatar = new JLabel(new CircleInitialsIcon(initials, 30, avatarBg, avatarFg));

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(TEXT_PRIMARY);

        JLabel lblCountry = new JLabel("[" + country + "]");
        lblCountry.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCountry.setForeground(TEXT_SECONDARY);

        nameBox.add(lblName);
        nameBox.add(lblCountry);

        left.add(avatar);
        left.add(nameBox);

        // Etiqueta de precio/crédito estilizada y alineada a la derecha
        JLabel lblLimit = new JLabel(creditLimit);
        lblLimit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLimit.setForeground(new Color(22, 101, 52));
        lblLimit.setBackground(new Color(240, 253, 244));
        lblLimit.setOpaque(true);
        lblLimit.setHorizontalAlignment(SwingConstants.RIGHT);
        lblLimit.setBorder(new EmptyBorder(4, 10, 4, 10));

        row.add(left, BorderLayout.CENTER);
        row.add(lblLimit, BorderLayout.EAST);

        return row;
    }

    private JPanel createAlertsCard() {
        RoundedCardPanel card = new RoundedCardPanel(14);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel title = new JLabel("Alertas y Tareas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_PRIMARY);

        JLabel pendingBadge = new JLabel("2 pendientes");
        pendingBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        pendingBadge.setForeground(ACCENT_AMBER);

        cardHeader.add(title, BorderLayout.WEST);
        cardHeader.add(pendingBadge, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        JPanel checkList = new JPanel();
        checkList.setLayout(new BoxLayout(checkList, BoxLayout.Y_AXIS));
        checkList.setOpaque(false);

        checkList.add(createAlertItem("Revisión de pedidos pendientes", "Pendiente", ACCENT_AMBER, false));
        checkList.add(Box.createRigidArea(new Dimension(0, 8)));
        checkList.add(createAlertItem("Reposición de stock gama Herramientas", "Urgente", ACCENT_RED, false));
        checkList.add(Box.createRigidArea(new Dimension(0, 8)));
        checkList.add(createAlertItem("Validación límite crédito cliente #1", "Completado", ACCENT_GREEN, true));

        card.add(checkList, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAlertItem(String taskText, String statusLabel, Color statusColor, boolean checked) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setOpaque(false);

        JCheckBox chk = new JCheckBox();
        chk.setSelected(checked);
        chk.setOpaque(false);

        JLabel text = new JLabel(taskText);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        text.setForeground(checked ? TEXT_SECONDARY : TEXT_PRIMARY);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        left.setOpaque(false);
        left.add(chk);
        left.add(text);

        JLabel badge = new JLabel(statusLabel);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(statusColor);

        item.add(left, BorderLayout.CENTER);
        item.add(badge, BorderLayout.EAST);

        return item;
    }

    // ==========================================
    // Componentes Visuales con Bordes Redondeados
    // ==========================================

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

            // Fondo Blanco de la Tarjeta
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            // Borde Sutil y Redondeado
            g2.setColor(CARD_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class SparklineComponent extends JComponent {
        private final boolean upward;

        public SparklineComponent(boolean upward) {
            this.upward = upward;
            setPreferredSize(new Dimension(45, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(34, 197, 94));
            g2.setStroke(new BasicStroke(1.8f));

            Path2D.Double path = new Path2D.Double();
            if (upward) {
                path.moveTo(2, 14);
                path.lineTo(12, 10);
                path.lineTo(22, 12);
                path.lineTo(32, 5);
                path.lineTo(42, 3);
            } else {
                path.moveTo(2, 3);
                path.lineTo(15, 7);
                path.lineTo(28, 6);
                path.lineTo(42, 14);
            }
            g2.draw(path);
            g2.dispose();
        }
    }

    private static class CircleIconBadge extends JPanel {
        public CircleIconBadge(Icon icon, Color bg, Color fg) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(42, 42));
            setOpaque(false);

            JLabel lbl = new JLabel(icon);
            lbl.setForeground(fg);
            lbl.setHorizontalAlignment(JLabel.CENTER);
            add(lbl, BorderLayout.CENTER);

            setBackground(bg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CircleInitialsIcon implements Icon {
        private final String initials;
        private final int size;
        private final Color bg, fg;

        public CircleInitialsIcon(String initials, int size, Color bg, Color fg) {
            this.initials = initials;
            this.size = size;
            this.bg = bg;
            this.fg = fg;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillOval(x, y, size, size);

            g2.setColor(fg);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            int sw = g2.getFontMetrics().stringWidth(initials);
            int sh = g2.getFontMetrics().getAscent();
            g2.drawString(initials, x + (size - sw) / 2, y + (size + sh) / 2 - 2);
            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    private static class UserBadgeIcon implements Icon {
        private final int w, h;
        public UserBadgeIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawOval(x + 5, y + 2, 8, 8);
            g2.drawArc(x + 2, y + 10, 14, 10, 0, 180);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class CartBadgeIcon implements Icon {
        private final int w, h;
        public CartBadgeIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRect(x + 2, y + 4, 12, 8);
            g2.drawOval(x + 4, y + 13, 3, 3);
            g2.drawOval(x + 10, y + 13, 3, 3);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class BoxBadgeIcon implements Icon {
        private final int w, h;
        public BoxBadgeIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRoundRect(x + 2, y + 3, 14, 12, 2, 2);
            g2.drawLine(x + 2, y + 7, x + 16, y + 7);
            g2.drawLine(x + 9, y + 7, x + 9, y + 15);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }
}
