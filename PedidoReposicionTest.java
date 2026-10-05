package com.sabordelsur.inventario.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias sobre el ciclo de vida de un PedidoReposicion (UC-09 / UC-10). */
class PedidoReposicionTest {

    private final Producto producto = new Producto(2, "Aceite de oliva 1L", null, 4, 5);

    @Test
    void unPedidoNuevoNaceConEstadoPendienteYFechaDeGeneracion() {
        PedidoReposicion pedido = new PedidoReposicion(producto);
        assertTrue(pedido.estaPendiente());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertNotNull(pedido.getFechaGeneracion());
    }

    @Test
    void resolver_retiraElPedidoDeLosPendientes() {
        PedidoReposicion pedido = new PedidoReposicion(producto);
        pedido.resolver();
        assertFalse(pedido.estaPendiente());
        assertEquals(EstadoPedido.RESUELTO, pedido.getEstado());
    }
}
