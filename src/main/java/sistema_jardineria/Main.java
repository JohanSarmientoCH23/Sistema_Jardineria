package sistema_jardineria;

import com.jardineria.infrastructure.adapters.out.persistence.ConexionMySQL;
import java.sql.Connection;

// import java.util.Arrays;
// import java.util.List;

public class Main {
    public static void main(String[] args) {
        //opciones labda
        // List<String> lista = Arrays.asList("uno", "dos", "tres");
        // lista.forEach(s -> System.out.println(s));
        Connection conn = ConexionMySQL.conectar();
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}