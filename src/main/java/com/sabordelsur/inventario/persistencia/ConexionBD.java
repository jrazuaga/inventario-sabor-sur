package com.sabordelsur.inventario.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único de acceso a la conexión JDBC contra la base de datos sabor_sur_db (MySQL),
 * definida en el script sabor_sur_db.sql (sección 11/12 del TP2).
 */
public final class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sabor_sur_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "cambiar_por_la_contrasena_local";

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
