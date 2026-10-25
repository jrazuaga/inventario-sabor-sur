package com.sabordelsur.inventario.modelo;

/** Resultado de evaluar la regla de reposición automática sobre un producto (UC-09 / UC-10). */
public enum AccionReposicion {
    /** El stock quedó por debajo del mínimo y se generó un pedido nuevo (UC-09). */
    PEDIDO_GENERADO,
    /** El stock volvió a superar el mínimo y se resolvió el pedido pendiente (UC-10). */
    PEDIDO_RESUELTO,
    /** No hizo falta generar ni resolver ningún pedido. */
    SIN_CAMBIOS
}
