package com.sabordelsur.inventario.estructuras;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColaEnlazadaTest {

    @Test
    void unaColaNuevaEstaVacia() {
        ColaEnlazada<String> cola = new ColaEnlazada<>();
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.tamanio());
    }

    @Test
    void desencolar_respetaElOrdenFifo() {
        ColaEnlazada<String> cola = new ColaEnlazada<>();
        cola.encolar("primero");
        cola.encolar("segundo");
        cola.encolar("tercero");
        assertEquals("primero", cola.desencolar());
        assertEquals("segundo", cola.desencolar());
        assertEquals("tercero", cola.desencolar());
        assertTrue(cola.estaVacia());
    }

    @Test
    void verFrente_noQuitaElElemento() {
        ColaEnlazada<String> cola = new ColaEnlazada<>();
        cola.encolar("a");
        cola.encolar("b");
        assertEquals("a", cola.verFrente());
        assertEquals(2, cola.tamanio());
    }

    @Test
    void sePuedeVolverAEncolarDespuesDeVaciarla() {
        ColaEnlazada<Integer> cola = new ColaEnlazada<>();
        cola.encolar(1);
        cola.desencolar();
        cola.encolar(2);
        assertFalse(cola.estaVacia());
        assertEquals(Integer.valueOf(2), cola.verFrente());
    }

    @Test
    void iterar_recorreDesdeElFrenteSinModificarLaCola() {
        ColaEnlazada<Integer> cola = new ColaEnlazada<>();
        cola.encolar(10);
        cola.encolar(20);
        StringBuilder recorrido = new StringBuilder();
        for (int valor : cola) {
            recorrido.append(valor).append(' ');
        }
        assertEquals("10 20 ", recorrido.toString());
        assertEquals(2, cola.tamanio());
    }

    @Test
    void desencolarOVerFrenteEnUnaColaVaciaLanzaExcepcion() {
        ColaEnlazada<String> cola = new ColaEnlazada<>();
        assertThrows(NoSuchElementException.class, cola::desencolar);
        assertThrows(NoSuchElementException.class, cola::verFrente);
    }
}
