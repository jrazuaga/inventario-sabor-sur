package com.sabordelsur.inventario.excepciones;

/**
 * Envuelve los errores técnicos de JDBC (SQLException) para que las capas superiores no dependan
 * de java.sql y puedan mostrar un mensaje comprensible para el usuario.
 */
public class PersistenciaException extends InventarioException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
