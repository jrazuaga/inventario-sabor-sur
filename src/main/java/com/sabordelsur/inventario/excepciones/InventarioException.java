package com.sabordelsur.inventario.excepciones;

/**
 * Excepción base (verificada) de todos los errores de negocio del sistema. Las demás excepciones
 * del paquete la extienden, de modo que la capa de presentación pueda capturarlas en conjunto o
 * por separado según necesite.
 */
public class InventarioException extends Exception {

    private static final long serialVersionUID = 1L;

    public InventarioException(String mensaje) {
        super(mensaje);
    }

    public InventarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
