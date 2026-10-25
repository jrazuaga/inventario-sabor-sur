package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Bloque de trabajo que se ejecuta dentro de una transacción. Se implementa con una expresión lambda
 * y lo recibe DAOBase.ejecutarEnTransaccion(), que se encarga del commit y del rollback.
 *
 * @param <R> tipo del resultado que devuelve la transacción
 */
@FunctionalInterface
public interface Transaccion<R> {

    R ejecutar(Connection con) throws SQLException, InventarioException;
}
