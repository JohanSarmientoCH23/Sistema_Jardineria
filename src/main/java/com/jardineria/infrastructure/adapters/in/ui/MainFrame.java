package com.jardineria.infrastructure.adapters.in.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.jardineria.infrastructure.adapters.in.ui.dashboard.DashboardPanel;
import com.jardineria.infrastructure.adapters.in.ui.views.CustomerView;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class MainFrame extends JFrame {

    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_CUSTOMERS = "CUSTOMERS";
    private static final String CARD_EMPLOYEES = "EMPLOYEES";
    private static final String CARD_PRODUCTS = "PRODUCTS";
    private static final String CARD_ORDERS = "ORDERS";

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainFrame() {
        FlatLightLaf.setup();
        initComponents();
    }

    private void initComponents() {
        setTitle("Gardening Management System - Hexagonal Architecture");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Navigation Panel (WEST)
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Content Area with CardLayout (CENTER)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add views to CardLayout
        contentPanel.add(new DashboardPanel(), CARD_DASHBOARD);
        contentPanel.add(new CustomerView(), CARD_CUSTOMERS);
        contentPanel.add(createPlaceholderView("Employees Management"), CARD_EMPLOYEES);
        contentPanel.add(createPlaceholderView("Products Catalog"), CARD_PRODUCTS);
        contentPanel.add(createPlaceholderView("Orders Processing"), CARD_ORDERS);

        // Default view: Dashboard
        cardLayout.show(contentPanel, CARD_DASHBOARD);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBackground(new Color(245, 247, 250));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 224, 230)));

        // Brand / Logo Header
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel brandLabel = new JLabel("Jardineria App");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandLabel.setForeground(new Color(33, 37, 41));

        JLabel brandSubtitle = new JLabel("Admin Portal");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSubtitle.setForeground(Color.GRAY);

        brandPanel.add(brandLabel, BorderLayout.NORTH);
        brandPanel.add(brandSubtitle, BorderLayout.SOUTH);
        sidebar.add(brandPanel);

        // Navigation Buttons
        sidebar.add(createNavButton("Dashboard", CARD_DASHBOARD));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createNavButton("Customers", CARD_CUSTOMERS));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createNavButton("Employees", CARD_EMPLOYEES));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createNavButton("Products", CARD_PRODUCTS));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createNavButton("Orders", CARD_ORDERS));

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(190, 42));
        button.setPreferredSize(new Dimension(190, 42));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));

        return button;
    }

    private JPanel createPlaceholderView(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel infoLabel = new JLabel("This module view is currently under development.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(Color.GRAY);

        JPanel header = new JPanel(new BorderLayout(5, 5));
        header.add(titleLabel, BorderLayout.NORTH);
        header.add(infoLabel, BorderLayout.SOUTH);

        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new MainFrame().setVisible(true);
        });
    }
}
