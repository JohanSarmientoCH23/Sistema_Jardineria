package com.jardineria.infrastructure.adapters.in.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.jardineria.infrastructure.adapters.in.ui.dashboard.DashboardPanel;
import com.jardineria.infrastructure.adapters.in.ui.views.CustomerView;
import com.jardineria.infrastructure.adapters.in.ui.views.EmployeeView;
import com.jardineria.infrastructure.adapters.in.ui.views.OfficeView;
import com.jardineria.infrastructure.adapters.in.ui.views.OrderDetailView;
import com.jardineria.infrastructure.adapters.in.ui.views.OrderView;
import com.jardineria.infrastructure.adapters.in.ui.views.PaymentView;
import com.jardineria.infrastructure.adapters.in.ui.views.ProductLineView;
import com.jardineria.infrastructure.adapters.in.ui.views.ProductView;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_CUSTOMERS = "CUSTOMERS";
    private static final String CARD_EMPLOYEES = "EMPLOYEES";
    private static final String CARD_PRODUCTS = "PRODUCTS";
    private static final String CARD_ORDERS = "ORDERS";
    private static final String CARD_OFFICES = "OFFICES";
    private static final String CARD_PRODUCT_LINES = "PRODUCT_LINES";
    private static final String CARD_PAYMENTS = "PAYMENTS";
    private static final String CARD_ORDER_DETAILS = "ORDER_DETAILS";

    // Corporate Palette
    private static final Color SIDEBAR_BG = new Color(24, 38, 57);
    private static final Color SIDEBAR_HOVER = new Color(34, 52, 77);
    private static final Color SIDEBAR_ACTIVE = new Color(41, 62, 92);
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color TEXT_LIGHT = new Color(241, 245, 249);

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private final List<NavButton> navButtons = new ArrayList<>();

    public MainFrame() {
        FlatLightLaf.setup();
        initComponents();
    }

    private void initComponents() {
        setTitle("Jardinería Enterprise System - Hexagonal Architecture");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Navigation Panel (WEST)
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Content Area with CardLayout (CENTER)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 246, 248));

        // Add views to CardLayout
        contentPanel.add(new DashboardPanel(), CARD_DASHBOARD);
        contentPanel.add(new CustomerView(), CARD_CUSTOMERS);
        contentPanel.add(new EmployeeView(), CARD_EMPLOYEES);
        contentPanel.add(new ProductView(), CARD_PRODUCTS);
        contentPanel.add(new OrderView(), CARD_ORDERS);
        contentPanel.add(new OfficeView(), CARD_OFFICES);
        contentPanel.add(new ProductLineView(), CARD_PRODUCT_LINES);
        contentPanel.add(new PaymentView(), CARD_PAYMENTS);
        contentPanel.add(new OrderDetailView(), CARD_ORDER_DETAILS);

        // Default view: Dashboard
        cardLayout.show(contentPanel, CARD_DASHBOARD);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(245, getHeight()));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(15, 23, 42)));

        // 1. Logo & Brand Header
        JPanel brandPanel = new JPanel(new BorderLayout(12, 0));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        // Geometric Leaf Icon
        JLabel logoIconLabel = new JLabel(new GeometricLeafIcon(28, 28));

        JPanel brandTextPanel = new JPanel();
        brandTextPanel.setLayout(new BoxLayout(brandTextPanel, BoxLayout.Y_AXIS));
        brandTextPanel.setOpaque(false);

        JLabel brandTitle = new JLabel("Jardineria App");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brandTitle.setForeground(TEXT_LIGHT);

        JLabel brandSubtitle = new JLabel("ENTERPRISE PORTAL");
        brandSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandSubtitle.setForeground(TEXT_MUTED);

        brandTextPanel.add(brandTitle);
        brandTextPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        brandTextPanel.add(brandSubtitle);

        brandPanel.add(logoIconLabel, BorderLayout.WEST);
        brandPanel.add(brandTextPanel, BorderLayout.CENTER);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // 2. Navigation Buttons Container
        JPanel navButtonsPanel = new JPanel();
        navButtonsPanel.setOpaque(false);
        navButtonsPanel.setLayout(new BoxLayout(navButtonsPanel, BoxLayout.Y_AXIS));
        navButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create Navigation Buttons
        addNavButton(navButtonsPanel, "Dashboard", CARD_DASHBOARD, new DashboardIcon(16, 16), true);
        addNavButton(navButtonsPanel, "Customers", CARD_CUSTOMERS, new CustomerIcon(16, 16), false);
        addNavButton(navButtonsPanel, "Employees", CARD_EMPLOYEES, new EmployeeIcon(16, 16), false);
        addNavButton(navButtonsPanel, "Products", CARD_PRODUCTS, new ProductIcon(16, 16), false);
        addNavButton(navButtonsPanel, "Orders", CARD_ORDERS, new OrderIcon(16, 16), false);
        addNavButton(navButtonsPanel, "Offices", CARD_OFFICES, new OfficeIcon(16, 16), false);
        addNavButton(navButtonsPanel, "Product Lines", CARD_PRODUCT_LINES, new ProductLineIcon(16, 16), false);
        addNavButton(navButtonsPanel, "Payments", CARD_PAYMENTS, new PaymentIcon(16, 16), false);
        addNavButton(navButtonsPanel, "Order Details", CARD_ORDER_DETAILS, new OrderDetailIcon(16, 16), false);

        JScrollPane scrollNav = new JScrollPane(navButtonsPanel);
        scrollNav.setOpaque(false);
        scrollNav.getViewport().setOpaque(false);
        scrollNav.setBorder(BorderFactory.createEmptyBorder());
        scrollNav.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        sidebar.add(scrollNav, BorderLayout.CENTER);

        // 3. User Profile Footer (Johan Admin)
        JPanel userFooter = createUserProfileFooter();
        sidebar.add(userFooter, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addNavButton(JPanel container, String text, String cardName, Icon icon, boolean isInitial) {
        NavButton button = new NavButton(text, icon);
        button.setSelected(isInitial);
        navButtons.add(button);

        button.addActionListener(e -> {
            for (NavButton b : navButtons) {
                b.setSelected(false);
            }
            button.setSelected(true);
            cardLayout.show(contentPanel, cardName);
        });

        container.add(button);
        container.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private JPanel createUserProfileFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout(12, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(34, 52, 77)),
                BorderFactory.createEmptyBorder(16, 18, 18, 18)
        ));

        // Circular Avatar "J"
        AvatarIcon avatar = new AvatarIcon("J", 36, new Color(41, 62, 92), Color.WHITE);
        JLabel avatarLabel = new JLabel(avatar);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Johan (Admin)");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_LIGHT);

        // Status badge with green dot
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusPanel.setOpaque(false);

        JLabel dotLabel = new JLabel(new StatusDotIcon(7, 7, new Color(34, 197, 94)));
        JLabel statusText = new JLabel("Online");
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusText.setForeground(TEXT_MUTED);

        statusPanel.add(dotLabel);
        statusPanel.add(statusText);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(statusPanel);

        footerPanel.add(avatarLabel, BorderLayout.WEST);
        footerPanel.add(infoPanel, BorderLayout.CENTER);

        return footerPanel;
    }

    // ==========================================
    // Custom Styled Navigation Button
    // ==========================================
    private static class NavButton extends JButton {
        private boolean isSelected = false;

        public NavButton(String text, Icon icon) {
            super(text, icon);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_MUTED);
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(12);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setMaximumSize(new Dimension(225, 40));
            setPreferredSize(new Dimension(225, 40));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 12));
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
            setForeground(selected ? Color.WHITE : TEXT_MUTED);
            setFont(new Font("Segoe UI", selected ? Font.BOLD : Font.PLAIN, 13));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                // Background when active
                g2.setColor(SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Indicator bar on left edge
                g2.setColor(ACCENT_GREEN);
                g2.fillRoundRect(2, 6, 4, getHeight() - 12, 3, 3);
            } else if (getModel().isRollover()) {
                // Background on hover
                g2.setColor(SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==========================================
    // Vector Icon Implementations (Zero External Assets Required)
    // ==========================================

    private static class GeometricLeafIcon implements Icon {
        private final int width, height;

        public GeometricLeafIcon(int w, int h) { this.width = w; this.height = h; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Subtle rounded diamond/leaf shape
            Path2D.Double leaf = new Path2D.Double();
            leaf.moveTo(x + width / 2.0, y + 2);
            leaf.curveTo(x + width - 2, y + 4, x + width - 2, y + height - 8, x + width / 2.0, y + height - 2);
            leaf.curveTo(x + 2, y + height - 8, x + 2, y + 4, x + width / 2.0, y + 2);
            leaf.closePath();

            g2.setColor(ACCENT_GREEN);
            g2.fill(leaf);

            // Leaf inner vein
            g2.setColor(new Color(24, 38, 57, 180));
            g2.drawLine(x + width / 2, y + 4, x + width / 2, y + height - 4);

            g2.dispose();
        }

        @Override public int getIconWidth() { return width; }
        @Override public int getIconHeight() { return height; }
    }

    private static class DashboardIcon implements Icon {
        private final int w, h;
        public DashboardIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRoundRect(x, y, 6, 6, 2, 2);
            g2.drawRoundRect(x + 8, y, 6, 6, 2, 2);
            g2.drawRoundRect(x, y + 8, 6, 6, 2, 2);
            g2.drawRoundRect(x + 8, y + 8, 6, 6, 2, 2);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class CustomerIcon implements Icon {
        private final int w, h;
        public CustomerIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawOval(x + 4, y + 1, 6, 6);
            g2.drawArc(x + 1, y + 8, 12, 9, 0, 180);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class EmployeeIcon implements Icon {
        private final int w, h;
        public EmployeeIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRoundRect(x + 1, y, 12, 14, 3, 3);
            g2.drawOval(x + 5, y + 3, 4, 4);
            g2.drawLine(x + 4, y + 10, x + 10, y + 10);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class ProductIcon implements Icon {
        private final int w, h;
        public ProductIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRoundRect(x + 1, y + 2, 12, 11, 2, 2);
            g2.drawLine(x + 1, y + 6, x + 13, y + 6);
            g2.drawLine(x + 7, y + 6, x + 7, y + 13);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class OrderIcon implements Icon {
        private final int w, h;
        public OrderIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRoundRect(x + 2, y + 1, 11, 13, 2, 2);
            g2.drawLine(x + 5, y + 5, x + 10, y + 5);
            g2.drawLine(x + 5, y + 8, x + 10, y + 8);
            g2.drawLine(x + 5, y + 11, x + 8, y + 11);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class OfficeIcon implements Icon {
        private final int w, h;
        public OfficeIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRect(x + 2, y + 2, 10, 12);
            g2.fillRect(x + 4, y + 4, 2, 2);
            g2.fillRect(x + 8, y + 4, 2, 2);
            g2.fillRect(x + 4, y + 8, 2, 2);
            g2.fillRect(x + 8, y + 8, 2, 2);
            g2.drawRect(x + 6, y + 11, 2, 3);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class ProductLineIcon implements Icon {
        private final int w, h;
        public ProductLineIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawOval(x + 2, y + 2, 10, 10);
            g2.drawOval(x + 5, y + 5, 4, 4);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class PaymentIcon implements Icon {
        private final int w, h;
        public PaymentIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.drawRoundRect(x + 1, y + 3, 13, 9, 2, 2);
            g2.drawLine(x + 1, y + 6, x + 14, y + 6);
            g2.fillRect(x + 3, y + 8, 3, 2);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class OrderDetailIcon implements Icon {
        private final int w, h;
        public OrderDetailIcon(int w, int h) { this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.fillOval(x + 2, y + 3, 2, 2);
            g2.drawLine(x + 6, y + 4, x + 13, y + 4);
            g2.fillOval(x + 2, y + 7, 2, 2);
            g2.drawLine(x + 6, y + 8, x + 13, y + 8);
            g2.fillOval(x + 2, y + 11, 2, 2);
            g2.drawLine(x + 6, y + 12, x + 13, y + 12);
            g2.dispose();
        }
        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    private static class AvatarIcon implements Icon {
        private final String text;
        private final int size;
        private final Color bg;
        private final Color fg;

        public AvatarIcon(String text, int size, Color bg, Color fg) {
            this.text = text;
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
            g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
            int strWidth = g2.getFontMetrics().stringWidth(text);
            int strHeight = g2.getFontMetrics().getAscent();
            g2.drawString(text, x + (size - strWidth) / 2, y + (size + strHeight) / 2 - 2);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    private static class StatusDotIcon implements Icon {
        private final int w, h;
        private final Color color;

        public StatusDotIcon(int w, int h, Color color) {
            this.w = w;
            this.h = h;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y, w, h);
            g2.dispose();
        }

        @Override public int getIconWidth() { return w; }
        @Override public int getIconHeight() { return h; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new MainFrame().setVisible(true);
        });
    }
}
