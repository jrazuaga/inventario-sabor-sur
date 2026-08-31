package com.sabordelsur.inventario.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias sobre la lógica de negocio pura de Producto (sin dependencia de la base de datos). */
class ProductoTest {

    @Test
    void actualizarStock_incrementaCorrectamente_conCantidadPositiva() {
        Producto producto = new Producto(1, "Aceite de girasol 5L", null, 10, 5);
        producto.actualizarStock(20);
        assertEquals(30, producto.getStockActual());
    }

    @Test
    void actualizarStock_decrementaCorrectamente_conCantidadNegativa() {
        Producto producto = new Producto(1, "Aceite de girasol 5L", null, 10, 5);
        producto.actualizarStock(-8);
        assertEquals(2, producto.getStockActual());
    }

    @Test
    void estaPorDebajoDelMinimo_esTrue_cuandoElStockActualEsMenor() {
        Producto producto = new Producto(2, "Aceite de oliva 1L", null, 4, 5);
        assertTrue(producto.estaPorDebajoDelMinimo());
    }

    @Test
    void estaPorDebajoDelMinimo_esFalse_cuandoElStockActualEsIgualAlMinimo() {
        Producto producto = new Producto(2, "Aceite de oliva 1L", null, 5, 5);
        assertFalse(producto.estaPorDebajoDelMinimo());
    }

    @Test
    void consultarStock_devuelveElStockActual() {
        Producto producto = new Producto(3, "Harina 0000 x25kg", null, 20, 15);
        assertEquals(20, producto.consultarStock());
    }
}
