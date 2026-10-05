package com.sabordelsur.inventario.modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Empleado de depósito: consulta stock y registra los movimientos de mercadería. */
public class Operador extends Usuario {

    private static final List<OpcionMenu> OPCIONES = Collections.unmodifiableList(Arrays.asList(
            OpcionMenu.CONSULTAR_STOCK,
            OpcionMenu.BUSCAR_PRODUCTO,
            OpcionMenu.REGISTRAR_MOVIMIENTO,
            OpcionMenu.VER_HISTORIAL_SESION));

    public Operador(int id, String nombreUsuario, String contrasena) {
        super(id, nombreUsuario, contrasena);
    }

    @Override
    public Rol getRol() {
        return Rol.OPERADOR;
    }

    @Override
    public List<OpcionMenu> getOpcionesPermitidas() {
        return OPCIONES;
    }
}
