package com.sabordelsur.inventario.seguridad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/** Pruebas unitarias sobre Hash. */
class HashTest {

    @Test
    void sha256_coincideConElVectorDePruebaConocido() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", Hash.sha256("abc"));
    }

    @Test
    void sha256_devuelve64CaracteresYDependeDelTexto() {
        assertEquals(64, Hash.sha256("operario123").length());
        assertNotEquals(Hash.sha256("operario123"), Hash.sha256("operario124"));
    }
}
