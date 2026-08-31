package com.sabordelsur.inventario.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias sobre Usuario.iniciarSesion() (UC-01), sin dependencia de la base de datos. */
class UsuarioTest {

    @Test
    void iniciarSesion_esTrue_conCredencialesCorrectas() {
        Usuario usuario = new Usuario(2, "operario1", "hash_operario1", Rol.OPERADOR);
        assertTrue(usuario.iniciarSesion("hash_operario1"));
    }

    @Test
    void iniciarSesion_esFalse_conContrasenaIncorrecta() {
        Usuario usuario = new Usuario(2, "operario1", "hash_operario1", Rol.OPERADOR);
        assertFalse(usuario.iniciarSesion("otra_contrasena"));
    }
}
