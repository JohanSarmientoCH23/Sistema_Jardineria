package sistema_jardineria;

import com.jardineria.infrastructure.adapters.in.ui.MainFrame;
import com.jardineria.infrastructure.adapters.out.persistence.ConexionMySQL;
import java.sql.Connection;

import javax.swing.SwingUtilities;

// import java.util.Arrays;
// import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setLocationRelativeTo(null); // Centra la ventana en la pantalla al abrir
            mainFrame.setVisible(true); // Hace visible la aplicación
        });
    }
}