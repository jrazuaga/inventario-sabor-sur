package com.sabordelsur.inventario.excepciones;

/**
 * Se lanza cuando un dato ingresado no cumple una regla del dominio (nombre vacío, precio negativo,
 * stock mínimo menor que cero, etc.). Se detecta antes de llegar a la base de datos, y también se usa
 * cuando MySQL rechaza un registro por una restricción CHECK.
 */
public class DatoInvalidoException extends InventarioException {

    private static final long serialVersionUID = 1L;

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public DatoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
