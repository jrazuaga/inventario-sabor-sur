package com.sabordelsur.inventario.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Resumen del estado del inventario. Combina las dos estructuras que pide la consigna: los productos
 * llegan en un ArrayList (su cantidad depende de lo que haya en la base), mientras que los contadores
 * por estado se guardan en un arreglo de tamaño fijo, indexado por el ordinal de EstadoStock, porque
 * la cantidad de estados posibles no cambia.
 */
public class ResumenInventario {

    private final int[] cantidadPorEstado = new int[EstadoStock.values().length];
    private final List<Producto> productos;

    public ResumenInventario(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
        for (Producto p : this.productos) {
            cantidadPorEstado[p.getEstadoStock().ordinal()]++;
        }
    }

    public int getCantidad(EstadoStock estado) {
        return cantidadPorEstado[estado.ordinal()];
    }

    /** Copia del arreglo de contadores, para que quien lo reciba no pueda modificar el original. */
    public int[] getCantidadPorEstado() {
        return cantidadPorEstado.clone();
    }

    public int getTotalProductos() {
        return productos.size();
    }

    /** Cantidad de productos que necesitan reposición (sin stock o por debajo del mínimo). */
    public int getProductosAReponer() {
        return getCantidad(EstadoStock.SIN_STOCK) + getCantidad(EstadoStock.BAJO_MINIMO);
    }

    /**
     * Devuelve, en un arreglo, los n productos más críticos (menor relación stock/mínimo). El tamaño
     * del resultado se conoce de antemano, por eso se usa un arreglo y no otra lista.
     */
    public Producto[] getMasCriticos(int n) {
        List<Producto> ordenados = new ArrayList<>(productos);
        ordenados.sort(Comparator.comparingDouble(Producto::getNivelCriticidad));
        Producto[] resultado = new Producto[Math.min(n, ordenados.size())];
        for (int i = 0; i < resultado.length; i++) {
            resultado[i] = ordenados.get(i);
        }
        return resultado;
    }
}
