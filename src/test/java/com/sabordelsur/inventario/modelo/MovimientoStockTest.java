package com.sabordelsur.inventario.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Pruebas unitarias sobre MovimientoStock.getVariacionStock() (UC-08). */
class MovimientoStockTest {

    @Test
    void getVariacionStock_esPositiva_paraUnaEntrada() {
        Producto producto = new Producto(1, "Aceite de girasol 5L", null, 12, 10);
        Usuario usuario = new Usuario(2, "operario1", "hash_operario1", Rol.OPERADOR);
        MovimientoStock movimiento = new MovimientoStock(producto, usuario, TipoMovimiento.ENTRADA, 15);
        assertEquals(15, movimiento.getVariacionStock());
    }

    @Test
    void getVariacionStock_esNegativa_paraUnaSalida() {
        Producto producto = new Producto(1, "Aceite de girasol 5L", null, 12, 10);
        Usuario usuario = new Usuario(2, "operario1", "hash_operario1", Rol.OPERADOR);
        MovimientoStock movimiento = new MovimientoStock(producto, usuario, TipoMovimiento.SALIDA, 6);
        assertEquals(-6, movimiento.getVariacionStock());
    }
}
