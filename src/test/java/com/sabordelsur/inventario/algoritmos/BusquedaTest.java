package com.sabordelsur.inventario.algoritmos;

import com.sabordelsur.inventario.modelo.Producto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusquedaTest {

    private final List<Producto> ordenados = Arrays.asList(
            new Producto(1, "Aceite de girasol 5L", null, 12, 10),
            new Producto(2, "Aceite de oliva 1L", null, 4, 5),
            new Producto(3, "Harina 0000 x25kg", null, 20, 15),
            new Producto(4, "Harina integral x25kg", null, 8, 10),
            new Producto(5, "Vaso descartable x50", null, 30, 20));

    @Test
    void binariaPorNombre_encuentraCadaProductoDeLaLista() {
        for (int i = 0; i < ordenados.size(); i++) {
            assertEquals(i, Busqueda.binariaPorNombre(ordenados, ordenados.get(i).getNombre()));
        }
    }

    @Test
    void binariaPorNombre_noDistingueMayusculas() {
        assertEquals(1, Busqueda.binariaPorNombre(ordenados, "ACEITE DE OLIVA 1L"));
    }

    @Test
    void binariaPorNombre_devuelveMenosUnoSiNoExiste() {
        assertEquals(-1, Busqueda.binariaPorNombre(ordenados, "Azúcar"));
        assertEquals(-1, Busqueda.binariaPorNombre(Collections.<Producto>emptyList(), "Aceite de oliva 1L"));
    }

    @Test
    void linealPorTexto_devuelveTodasLasCoincidenciasParciales() {
        List<Producto> resultado = Busqueda.linealPorTexto(ordenados, "harina");
        assertEquals(2, resultado.size());
        assertEquals("Harina 0000 x25kg", resultado.get(0).getNombre());
        assertEquals("Harina integral x25kg", resultado.get(1).getNombre());
    }

    @Test
    void linealPorTexto_devuelveListaVaciaSiNadaCoincide() {
        assertTrue(Busqueda.linealPorTexto(ordenados, "azúcar").isEmpty());
    }
}
