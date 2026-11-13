package com.jardineria.infrastructure.adapters.in.ui.dashboard;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header Panel with Title and Subtitle
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("System Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitleLabel = new JLabel("Overview of sales and operations performance");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Chart Section
        JFreeChart barChart = createSalesByProductLineChart();
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createSalesByProductLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sample static sales data by product line
        dataset.addValue(38200.00, "Sales ($)", "Arboles");
        dataset.addValue(24800.00, "Sales ($)", "Herramientas");
        dataset.addValue(19500.00, "Sales ($)", "Frutales");
        dataset.addValue(15400.00, "Sales ($)", "Herbaceas");
        dataset.addValue(12300.00, "Sales ($)", "Aromaticas");

        JFreeChart chart = ChartFactory.createBarChart(
                "Sales by Product Line",
                "Product Line",
                "Total Sales (USD)",
                dataset,
                PlotOrientation.VERTICAL,
                false,  // include legend
                true,   // tooltips
                false   // urls
        );

        // Chart styling
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        chart.setBackgroundPaint(getBackground());

        return chart;
    }
}
