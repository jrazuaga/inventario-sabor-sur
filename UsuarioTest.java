package com.sabordelsur.inventario.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias sobre Usuario y sus subclases (UC-01, RF-08), sin dependencia de la base de datos. */
class UsuarioTest {

    @Test
    void iniciarSesion_esTrue_conCredencialesCorrectas() {
        Usuario usuario = new Operador(2, "operario1", "hash_operario1");
        assertTrue(usuario.iniciarSesion("hash_operario1"));
    }

    @Test
    void iniciarSesion_esFalse_conContrasenaIncorrecta() {
        Usuario usuario = new Operador(2, "operario1", "hash_operario1");
        assertFalse(usuario.iniciarSesion("otra_contrasena"));
    }

    @Test
    void crear_devuelveLaSubclaseQueCorrespondeAlRol() {
        assertTrue(Usuario.crear(Rol.ADMINISTRADOR, 1, "propietario", "x") instanceof Administrador);
        assertTrue(Usuario.crear(Rol.OPERADOR, 2, "operario1", "x") instanceof Operador);
    }

    @Test
    void polimorfismo_cadaSubclaseDefineSusPropiasOpcionesDeMenu() {
        Usuario admin = new Administrador(1, "propietario", "x");
        Usuario operador = new Operador(2, "operario1", "x");

        assertEquals(Rol.ADMINISTRADOR, admin.getRol());
        assertEquals(Rol.OPERADOR, operador.getRol());
        assertTrue(admin.puedeAcceder(OpcionMenu.COMPARAR_PROVEEDORES));
        assertFalse(admin.puedeAcceder(OpcionMenu.REGISTRAR_MOVIMIENTO));
        assertTrue(operador.puedeAcceder(OpcionMenu.REGISTRAR_MOVIMIENTO));
        assertFalse(operador.puedeAcceder(OpcionMenu.VER_COLA_PEDIDOS));
    }

    @Test
    void ambosRolesPuedenConsultarStock() {
        assertTrue(new Administrador(1, "propietario", "x").puedeAcceder(OpcionMenu.CONSULTAR_STOCK));
        assertTrue(new Operador(2, "operario1", "x").puedeAcceder(OpcionMenu.CONSULTAR_STOCK));
    }

    @Test
    void lasOpcionesPermitidasNoSePuedenModificarDesdeAfuera() {
        Usuario operador = new Operador(2, "operario1", "x");
        assertThrows(UnsupportedOperationException.class,
                () -> operador.getOpcionesPermitidas().add(OpcionMenu.VER_COLA_PEDIDOS));
    }
}
