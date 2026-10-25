package com.sabordelsur.inventario.modelo;

/**
 * Opciones del sistema. Cada tipo de usuario habilita un subconjunto distinto (RF-08). Las opciones de
 * gestión (ABM) y de reportes solo se ofrecen en la interfaz gráfica; el resto está disponible también
 * en el modo consola.
 */
public enum OpcionMenu {
    CONSULTAR_STOCK("Consultar stock de productos (UC-11)", true),
    BUSCAR_PRODUCTO("Buscar un producto por nombre (UC-11)", true),
    COMPARAR_PROVEEDORES("Comparar condiciones de proveedores (UC-05)", true),
    VER_COLA_PEDIDOS("Ver la cola de pedidos de reposición (UC-07)", true),
    VER_PRODUCTOS_CRITICOS("Ver productos ordenados por criticidad de stock (UC-11)", true),
    REGISTRAR_MOVIMIENTO("Registrar un movimiento de stock (UC-08)", true),
    VER_HISTORIAL_SESION("Ver los movimientos registrados en esta sesión (UC-08)", true),
    GESTIONAR_PRODUCTOS("Gestionar productos y categorías (UC-02)", false),
    GESTIONAR_PROVEEDORES("Gestionar proveedores y sus ofertas (UC-03, UC-04)", false),
    GESTIONAR_USUARIOS("Gestionar usuarios y roles (UC-06)", false),
    REPORTES("Exportar reportes y consultar la bitácora", false);

    private final String descripcion;
    private final boolean disponibleEnConsola;

    OpcionMenu(String descripcion, boolean disponibleEnConsola) {
        this.descripcion = descripcion;
        this.disponibleEnConsola = disponibleEnConsola;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isDisponibleEnConsola() {
        return disponibleEnConsola;
    }
}
