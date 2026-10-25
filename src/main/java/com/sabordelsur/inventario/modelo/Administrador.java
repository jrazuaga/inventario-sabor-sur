package com.sabordelsur.inventario.modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * El propietario de la distribuidora: consulta, compara proveedores, supervisa la cola de reposición y
 * administra los datos maestros (productos, proveedores, usuarios) y los reportes.
 */
public class Administrador extends Usuario {

    private static final List<OpcionMenu> OPCIONES = Collections.unmodifiableList(Arrays.asList(
            OpcionMenu.CONSULTAR_STOCK,
            OpcionMenu.BUSCAR_PRODUCTO,
            OpcionMenu.COMPARAR_PROVEEDORES,
            OpcionMenu.VER_COLA_PEDIDOS,
            OpcionMenu.VER_PRODUCTOS_CRITICOS,
            OpcionMenu.GESTIONAR_PRODUCTOS,
            OpcionMenu.GESTIONAR_PROVEEDORES,
            OpcionMenu.GESTIONAR_USUARIOS,
            OpcionMenu.REPORTES));

    public Administrador(int id, String nombreUsuario, String hashContrasena) {
        super(id, nombreUsuario, hashContrasena);
    }

    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }

    @Override
    public List<OpcionMenu> getOpcionesPermitidas() {
        return OPCIONES;
    }
}
