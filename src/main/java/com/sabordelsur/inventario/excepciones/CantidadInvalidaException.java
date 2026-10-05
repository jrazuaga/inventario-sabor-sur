package com.sabordelsur.inventario.excepciones;

/** Se lanza cuando la cantidad de un movimiento de stock no es un número positivo. */
public class CantidadInvalidaException extends InventarioException {

    private static final long serialVersionUID = 1L;

    public CantidadInvalidaException(int cantidad) {
        super("La cantidad del movimiento debe ser mayor que cero (se ingresó " + cantidad + ").");
    }
}
