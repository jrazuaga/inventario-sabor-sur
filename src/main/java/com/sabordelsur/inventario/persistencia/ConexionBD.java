package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.archivos.ConfiguracionBD;
import com.sabordelsur.inventario.excepciones.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único de acceso a la conexión JDBC contra la base de datos sabor_sur_db (MySQL), definida en el
 * script sabor_sur_db.sql. Los datos de conexión se toman, en este orden, de las variables de entorno
 * (SABOR_SUR_DB_URL, SABOR_SUR_DB_USER, SABOR_SUR_DB_PASSWORD), del archivo config/sabor_sur.properties
 * o de los valores por defecto de una instalación local de MySQL.
 */
public final class ConexionBD {

    private static final ConfiguracionBD CONFIG = ConfiguracionBD.cargar(ConfiguracionBD.ARCHIVO_POR_DEFECTO);

    private static final String URL = CONFIG.obtener("SABOR_SUR_DB_URL", "db.url",
            "jdbc:mysql://localhost:3306/sabor_sur_db?useSSL=false&serverTimezone=UTC");
    private static final String USUARIO = CONFIG.obtener("SABOR_SUR_DB_USER", "db.usuario", "root");
    private static final String CONTRASENA = CONFIG.obtener("SABOR_SUR_DB_PASSWORD", "db.contrasena", "");

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws PersistenciaException {
        try {
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo conectar con la base de datos. Verifique que MySQL esté activo "
                            + "y que los datos de config/sabor_sur.properties sean correctos.", e);
        }
    }

    /** Indica si la base responde. Se usa al iniciar la interfaz y en las pruebas de integración. */
    public static boolean estaDisponible() {
        try (Connection con = obtenerConexion()) {
            return con.isValid(2);
        } catch (PersistenciaException | SQLException e) {
            return false;
        }
    }

    /** URL de conexión sin credenciales, para mostrarla en la barra de estado. */
    public static String getUrl() {
        return URL;
    }
}
