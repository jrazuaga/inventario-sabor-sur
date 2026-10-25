package com.sabordelsur.inventario.excepciones;

/**
 * Traduce el error 1062 de MySQL (clave duplicada): se intentó registrar un nombre de producto,
 * proveedor, categoría o usuario que ya existe.
 */
public class DatoDuplicadoException extends PersistenciaException {

    private static final long serialVersionUID = 1L;

    public DatoDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
