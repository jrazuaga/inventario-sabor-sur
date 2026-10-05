package com.sabordelsur.inventario.modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** El propietario de la distribuidora: consulta, compara proveedores y supervisa la cola de reposición. */
public class Administrador extends Usuario {

    private static final List<OpcionMenu> OPCIONES = Collections.unmodifiableList(Arrays.asList(
            OpcionMenu.CONSULTAR_STOCK,
            OpcionMenu.BUSCAR_PRODUCTO,
            OpcionMenu.COMPARAR_PROVEEDORES,
            OpcionMenu.VER_COLA_PEDIDOS,
            OpcionMenu.VER_PRODUCTOS_CRITICOS));

    public Administrador(int id, String nombreUsuario, String contrasena) {
        super(id, nombreUsuario, contrasena);
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
