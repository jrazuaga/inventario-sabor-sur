package com.sabordelsur.inventario.algoritmos;

import com.sabordelsur.inventario.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

/** Algoritmos de búsqueda sobre listas de productos. */
public final class Busqueda {

    private Busqueda() {
    }

    /**
     * Búsqueda binaria de un producto por nombre exacto (sin distinguir mayúsculas). La lista debe estar
     * ordenada alfabéticamente por nombre, que es el orden natural de Producto. Devuelve la posición
     * encontrada o -1 si no existe. Complejidad O(log n).
     */
    public static int binariaPorNombre(List<Producto> ordenadaPorNombre, String nombre) {
        int inferior = 0;
        int superior = ordenadaPorNombre.size() - 1;
        while (inferior <= superior) {
            int medio = (inferior + superior) / 2;
            int comparacion = String.CASE_INSENSITIVE_ORDER.compare(
                    ordenadaPorNombre.get(medio).getNombre(), nombre);
            if (comparacion == 0) {
                return medio;
            } else if (comparacion < 0) {
                inferior = medio + 1;
            } else {
                superior = medio - 1;
            }
        }
        return -1;
    }

    /**
     * Búsqueda lineal de todos los productos cuyo nombre contiene el texto indicado (sin distinguir
     * mayúsculas). No requiere que la lista esté ordenada. Complejidad O(n).
     */
    public static List<Producto> linealPorTexto(List<Producto> productos, String fragmento) {
        List<Producto> coincidencias = new ArrayList<>();
        String buscado = fragmento.trim().toLowerCase();
        for (Producto producto : productos) {
            if (producto.getNombre().toLowerCase().contains(buscado)) {
                coincidencias.add(producto);
            }
        }
        return coincidencias;
    }
}
