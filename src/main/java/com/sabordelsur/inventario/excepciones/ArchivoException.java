package com.sabordelsur.inventario.excepciones;

/** Envuelve los errores de entrada/salida al leer o escribir archivos (reportes CSV, bitácora, configuración). */
public class ArchivoException extends InventarioException {

    private static final long serialVersionUID = 1L;

    public ArchivoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
