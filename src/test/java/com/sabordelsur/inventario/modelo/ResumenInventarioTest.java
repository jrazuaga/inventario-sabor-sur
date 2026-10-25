package com.sabordelsur.inventario.modelo;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumenInventarioTest {

    private final Categoria c = new Categoria(1, "Varios");
    private final List<Producto> productos = Arrays.asList(
            new Producto(1, "A", c, 0, 5),     // sin stock
            new Producto(2, "B", c, 3, 5),     // bajo el mínimo
            new Producto(3, "C", c, 9, 5),     // normal
            new Producto(4, "D", c, 6, 5));    // normal

    @Test
    void cuentaLosProductosPorEstadoEnElArreglo() {
        ResumenInventario resumen = new ResumenInventario(productos);
        assertEquals(1, resumen.getCantidad(EstadoStock.SIN_STOCK));
        assertEquals(1, resumen.getCantidad(EstadoStock.BAJO_MINIMO));
        assertEquals(2, resumen.getCantidad(EstadoStock.NORMAL));
        assertEquals(2, resumen.getProductosAReponer());
        assertEquals(4, resumen.getTotalProductos());
    }

    @Test
    void elArregloDevueltoEsUnaCopia() {
        ResumenInventario resumen = new ResumenInventario(productos);
        resumen.getCantidadPorEstado()[EstadoStock.NORMAL.ordinal()] = 99;
        assertEquals(2, resumen.getCantidad(EstadoStock.NORMAL));
    }

    @Test
    void masCriticosDevuelveUnArregloOrdenadoYLimitado() {
        Producto[] criticos = new ResumenInventario(productos).getMasCriticos(2);
        assertEquals(2, criticos.length);
        assertEquals("A", criticos[0].getNombre());
        assertEquals("B", criticos[1].getNombre());
        assertEquals(4, new ResumenInventario(productos).getMasCriticos(10).length);
    }
}
