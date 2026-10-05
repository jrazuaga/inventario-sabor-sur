package com.sabordelsur.inventario.excepciones;

/** Se lanza cuando se busca un producto por su identificador y no existe en la base. */
public class ProductoNoEncontradoException extends InventarioException {

    private static final long serialVersionUID = 1L;

    public ProductoNoEncontradoException(int idProducto) {
        super("No existe ningún producto con el ID " + idProducto + ".");
    }
}
