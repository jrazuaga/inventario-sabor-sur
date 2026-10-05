package com.sabordelsur.inventario.modelo;

/** Opciones del menú principal. Cada tipo de usuario habilita un subconjunto distinto (RF-08). */
public enum OpcionMenu {
    CONSULTAR_STOCK("Consultar stock de productos (UC-11)"),
    BUSCAR_PRODUCTO("Buscar un producto por nombre (UC-11)"),
    COMPARAR_PROVEEDORES("Comparar condiciones de proveedores (UC-05)"),
    VER_COLA_PEDIDOS("Ver la cola de pedidos de reposición (UC-07)"),
    VER_PRODUCTOS_CRITICOS("Ver productos ordenados por criticidad de stock (UC-11)"),
    REGISTRAR_MOVIMIENTO("Registrar un movimiento de stock (UC-08)"),
    VER_HISTORIAL_SESION("Ver los movimientos registrados en esta sesión (UC-08)");

    private final String descripcion;

    OpcionMenu(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
