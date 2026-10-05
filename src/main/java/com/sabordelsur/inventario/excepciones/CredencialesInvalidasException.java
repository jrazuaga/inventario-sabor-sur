package com.sabordelsur.inventario.excepciones;

/** Se lanza cuando el usuario o la contraseña ingresados no coinciden con un usuario registrado (UC-01). */
public class CredencialesInvalidasException extends InventarioException {

    private static final long serialVersionUID = 1L;

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos.");
    }
}
