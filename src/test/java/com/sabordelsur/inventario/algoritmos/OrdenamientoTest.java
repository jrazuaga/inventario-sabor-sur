package com.sabordelsur.inventario.algoritmos;

import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdenamientoTest {

    private static ProductoProveedor oferta(double precio, int plazo) {
        return new ProductoProveedor(null, null, precio, plazo, true);
    }

    @Test
    void ordenarPorInsercion_ordenaOfertasPorPrecioAscendente() {
        List<ProductoProveedor> ofertas = new ArrayList<>(Arrays.asList(oferta(6800, 3), oferta(6500, 7), oferta(7200, 1)));
        Ordenamiento.ordenarPorInsercion(ofertas, ProductoProveedor.POR_PRECIO);
        assertEquals(6500, ofertas.get(0).getPrecio(), 0.001);
        assertEquals(6800, ofertas.get(1).getPrecio(), 0.001);
        assertEquals(7200, ofertas.get(2).getPrecio(), 0.001);
    }

    @Test
    void ordenarPorInsercion_permiteCambiarElCriterio() {
        List<ProductoProveedor> ofertas = new ArrayList<>(Arrays.asList(oferta(6800, 3), oferta(6500, 7), oferta(7200, 1)));
        Ordenamiento.ordenarPorInsercion(ofertas, ProductoProveedor.POR_PLAZO);
        assertEquals(1, ofertas.get(0).getPlazoEntregaDias());
        assertEquals(7, ofertas.get(2).getPlazoEntregaDias());
    }

    @Test
    void ordenarPorInsercion_esEstable() {
        List<ProductoProveedor> ofertas = new ArrayList<>(Arrays.asList(oferta(100, 5), oferta(100, 2), oferta(100, 9)));
        Ordenamiento.ordenarPorInsercion(ofertas, ProductoProveedor.POR_PRECIO);
        assertEquals(5, ofertas.get(0).getPlazoEntregaDias());
        assertEquals(2, ofertas.get(1).getPlazoEntregaDias());
        assertEquals(9, ofertas.get(2).getPlazoEntregaDias());
    }

    @Test
    void ordenarPorMezcla_ordenaProductosPorNombre_yNoModificaLaOriginal() {
        List<Producto> original = Arrays.asList(
                new Producto(3, "Vaso descartable x50", null, 1, 1),
                new Producto(1, "Aceite de oliva 1L", null, 1, 1),
                new Producto(2, "Harina 0000 x25kg", null, 1, 1));
        List<Producto> ordenada = Ordenamiento.ordenarPorMezcla(original, Comparator.naturalOrder());
        assertEquals("Aceite de oliva 1L", ordenada.get(0).getNombre());
        assertEquals("Harina 0000 x25kg", ordenada.get(1).getNombre());
        assertEquals("Vaso descartable x50", ordenada.get(2).getNombre());
        assertEquals("Vaso descartable x50", original.get(0).getNombre());
    }

    @Test
    void ordenarPorMezcla_coincideConElOrdenamientoDeLaLibreria_enUnaListaGrande() {
        List<Integer> numeros = new ArrayList<>();
        int semilla = 7;
        for (int i = 0; i < 500; i++) {
            semilla = (semilla * 31 + 17) % 1009;
            numeros.add(semilla);
        }
        List<Integer> esperado = new ArrayList<>(numeros);
        Collections.sort(esperado);
        assertEquals(esperado, Ordenamiento.ordenarPorMezcla(numeros, Comparator.naturalOrder()));
    }

    @Test
    void ordenarPorMezcla_aceptaListasVaciasOConUnElemento() {
        assertTrue(Ordenamiento.ordenarPorMezcla(new ArrayList<Integer>(), Comparator.naturalOrder()).isEmpty());
        assertEquals(Collections.singletonList(5),
                Ordenamiento.ordenarPorMezcla(Collections.singletonList(5), Comparator.naturalOrder()));
    }

    @Test
    void ordenarPorMezcla_ordenaProductosPorCriticidad() {
        Producto critico = new Producto(2, "Aceite de oliva 1L", null, 1, 5);
        Producto medio = new Producto(4, "Harina integral x25kg", null, 8, 10);
        Producto holgado = new Producto(5, "Vaso descartable x50", null, 30, 20);
        List<Producto> ordenada = Ordenamiento.ordenarPorMezcla(
                Arrays.asList(holgado, critico, medio), Comparator.comparingDouble(Producto::getNivelCriticidad));
        assertEquals(critico, ordenada.get(0));
        assertEquals(medio, ordenada.get(1));
        assertEquals(holgado, ordenada.get(2));
    }
}
