package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ControladorUsuariosTest {

    @Test
    void nombreDeUsuarioValido() {
        assertDoesNotThrow(() -> ControladorUsuarios.validarNombre("operario3"));
    }

    @Test
    void nombreConEspaciosOMuyCortoSeRechaza() {
        assertThrows(DatoInvalidoException.class, () -> ControladorUsuarios.validarNombre("ana perez"));
        assertThrows(DatoInvalidoException.class, () -> ControladorUsuarios.validarNombre("ab"));
    }

    @Test
    void contrasenaCortaSeRechaza() {
        assertThrows(DatoInvalidoException.class, () -> ControladorUsuarios.validarContrasena("1234567"));
        assertDoesNotThrow(() -> ControladorUsuarios.validarContrasena("12345678"));
    }
}
