package com.jardineria.infrastructure.adapters.out.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {
    // Conexión configurada con UTF-8 explícito para preservar tildes, eñes y caracteres especiales
    private static final String URL = "jdbc:mysql://localhost:3307/jardineria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "root2026"; 

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de MySQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL");
            e.printStackTrace();
        }
        return conexion;
    }
}