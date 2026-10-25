package com.sabordelsur.inventario.excepciones;

/**
 * Traduce el error 1451 de MySQL (violación de clave foránea al borrar): el registro que se quiere
 * eliminar todavía está referenciado, por ejemplo un producto con movimientos de stock registrados.
 */
public class RegistroEnUsoException extends PersistenciaException {

    private static final long serialVersionUID = 1L;

    public RegistroEnUsoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
