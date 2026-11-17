package sistema_jardineria;

import com.formdev.flatlaf.FlatLightLaf;
import com.jardineria.infrastructure.adapters.in.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

public class Main {
    public static void main(String[] args) {
        // 1. Configurar FlatLaf Base
        FlatLightLaf.setup();

        // 2. Estilos Globales inspirados en la estética moderna de Verde Vivo
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ProgressBar.arc", 8);

        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        // Estilos de Tablas
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.rowHeight", 36);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Table.gridColor", new Color(241, 245, 249));
        UIManager.put("Table.alternateRowColor", new Color(248, 250, 252));
        UIManager.put("Table.selectionBackground", new Color(236, 253, 243));
        UIManager.put("Table.selectionForeground", new Color(22, 101, 52));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TableHeader.background", new Color(248, 250, 252));
        UIManager.put("TableHeader.foreground", new Color(71, 85, 105));
        UIManager.put("TableHeader.bottomSeparatorColor", new Color(226, 232, 240));

        // Barras de desplazamiento suaves
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 10);

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}