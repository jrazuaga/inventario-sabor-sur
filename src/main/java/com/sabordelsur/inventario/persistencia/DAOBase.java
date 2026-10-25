package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.DatoDuplicadoException;
import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.excepciones.RegistroEnUsoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta con el código JDBC que comparten todos los DAO: abrir la conexión, asignar los
 * parámetros del PreparedStatement, recorrer el ResultSet, manejar transacciones y traducir las
 * SQLException a excepciones del dominio. Cada DAO concreto solo define cómo se arma un objeto a
 * partir de una fila (mapear) y qué sentencias SQL ejecuta.
 *
 * @param <T> entidad del modelo que maneja el DAO
 */
public abstract class DAOBase<T> {

    // Códigos de error de MySQL que se traducen a excepciones propias.
    static final int ER_DUP_ENTRY = 1062;
    static final int ER_ROW_IS_REFERENCED = 1451;
    static final int ER_ROW_IS_REFERENCED_ANTIGUO = 1217;
    static final int ER_CHECK_CONSTRAINT_MYSQL = 3819;
    static final int ER_CHECK_CONSTRAINT_MARIADB = 4025;

    /** Convierte la fila actual del ResultSet en un objeto del modelo. */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    /** Nombre de la entidad en singular, para armar mensajes de error comprensibles. */
    protected abstract String getNombreEntidad();

    /** Ejecuta una consulta y devuelve todas las filas mapeadas. */
    protected List<T> consultarLista(String sql, Object... parametros) throws PersistenciaException {
        List<T> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar " + getNombreEntidad() + ".", e);
        }
        return resultado;
    }

    /** Ejecuta una consulta que devuelve a lo sumo una fila. Si no hay resultados devuelve null. */
    protected T consultarUno(String sql, Object... parametros) throws PersistenciaException {
        List<T> lista = consultarLista(sql, parametros);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Ejecuta un INSERT, UPDATE o DELETE y devuelve la cantidad de filas afectadas. */
    protected int ejecutarActualizacion(String operacion, String sql, Object... parametros)
            throws InventarioException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarParametros(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw traducir(e, operacion);
        }
    }

    /** Ejecuta un INSERT y devuelve la clave autoincremental generada por MySQL. */
    protected int insertarYObtenerId(Connection con, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(ps, parametros);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : 0;
            }
        }
    }

    /**
     * Ejecuta el bloque recibido dentro de una transacción: si termina bien hace commit; ante cualquier
     * excepción hace rollback, de modo que la base nunca queda con cambios a medias.
     */
    protected <R> R ejecutarEnTransaccion(String operacion, Transaccion<R> trabajo)
            throws InventarioException {
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                R resultado = trabajo.ejecutar(con);
                con.commit();
                return resultado;
            } catch (SQLException e) {
                revertir(con);
                throw traducir(e, operacion);
            } catch (InventarioException | RuntimeException e) {
                revertir(con);
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de conexión al " + operacion + ".", e);
        }
    }

    /**
     * Asigna los parámetros (recibidos como arreglo, mediante varargs) a los signos "?" de la sentencia,
     * en el mismo orden. Usar siempre PreparedStatement evita la inyección SQL.
     */
    protected static void asignarParametros(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
    }

    /** Traduce una SQLException a la excepción del dominio que corresponde según el código de error. */
    protected InventarioException traducir(SQLException e, String operacion) {
        switch (e.getErrorCode()) {
            case ER_DUP_ENTRY:
                return new DatoDuplicadoException(
                        "Ya existe un registro de " + getNombreEntidad() + " con ese nombre.", e);
            case ER_ROW_IS_REFERENCED:
            case ER_ROW_IS_REFERENCED_ANTIGUO:
                return new RegistroEnUsoException("No se puede " + operacion + " porque tiene movimientos "
                        + "de stock u otros registros asociados, que deben conservarse para mantener "
                        + "la trazabilidad.", e);
            case ER_CHECK_CONSTRAINT_MYSQL:
            case ER_CHECK_CONSTRAINT_MARIADB:
                return new DatoInvalidoException("La base de datos rechazó el valor ingresado para "
                        + getNombreEntidad() + ".", e);
            default:
                return new PersistenciaException("Error al " + operacion + ".", e);
        }
    }

    private static void revertir(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ignorada) {
            // Si el rollback falla, la conexión se cierra igual y el motor descarta la transacción abierta.
        }
    }
}
