package com.sabordelsur.inventario.estructuras;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PilaEnlazadaTest {

    @Test
    void desapilar_respetaElOrdenLifo() {
        PilaEnlazada<String> pila = new PilaEnlazada<>();
        pila.apilar("primero");
        pila.apilar("segundo");
        pila.apilar("tercero");
        assertEquals("tercero", pila.desapilar());
        assertEquals("segundo", pila.desapilar());
        assertEquals("primero", pila.desapilar());
        assertTrue(pila.estaVacia());
    }

    @Test
    void verTope_noQuitaElElemento() {
        PilaEnlazada<Integer> pila = new PilaEnlazada<>();
        pila.apilar(1);
        pila.apilar(2);
        assertEquals(Integer.valueOf(2), pila.verTope());
        assertEquals(2, pila.tamanio());
    }

    @Test
    void iterar_recorreDesdeElTopeHaciaLaBase() {
        PilaEnlazada<Integer> pila = new PilaEnlazada<>();
        pila.apilar(1);
        pila.apilar(2);
        pila.apilar(3);
        StringBuilder recorrido = new StringBuilder();
        for (int valor : pila) {
            recorrido.append(valor).append(' ');
        }
        assertEquals("3 2 1 ", recorrido.toString());
    }

    @Test
    void desapilarOVerTopeEnUnaPilaVaciaLanzaExcepcion() {
        PilaEnlazada<String> pila = new PilaEnlazada<>();
        assertThrows(NoSuchElementException.class, pila::desapilar);
        assertThrows(NoSuchElementException.class, pila::verTope);
    }
}
