package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único de acceso a la conexión JDBC contra la base de datos sabor_sur_db (MySQL),
 * definida en el script sabor_sur_db.sql. Los datos de conexión se toman de variables de entorno
 * (SABOR_SUR_DB_URL, SABOR_SUR_DB_USER, SABOR_SUR_DB_PASSWORD); si no están definidas, se usan los
 * valores por defecto de una instalación local de MySQL, que deben ajustarse a cada equipo.
 */
public final class ConexionBD {

    private static final String URL = leerVariable("SABOR_SUR_DB_URL",
            "jdbc:mysql://localhost:3306/sabor_sur_db?useSSL=false&serverTimezone=UTC");
    private static final String USUARIO = leerVariable("SABOR_SUR_DB_USER", "root");
    private static final String CONTRASENA = leerVariable("SABOR_SUR_DB_PASSWORD", "cambiar_por_la_contrasena_local");

    private ConexionBD() {
    }

    private static String leerVariable(String nombre, String valorPorDefecto) {
        String valor = System.getenv(nombre);
        return (valor == null || valor.isEmpty()) ? valorPorDefecto : valor;
    }

    public static Connection obtenerConexion() throws PersistenciaException {
        try {
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo conectar con la base de datos. Verifique que MySQL esté activo y que los datos "
                            + "de conexión sean correctos.", e);
        }
    }
}
