package sistema_jardineria;

import com.jardineria.infrastructure.adapters.out.persistence.ConexionMySQL;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
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