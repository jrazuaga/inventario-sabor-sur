package com.sabordelsur.inventario.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Algoritmos de ordenación implementados a mano. Ambos son estables: dos elementos que el comparador
 * considera iguales conservan el orden relativo que tenían.
 */
public final class Ordenamiento {

    private Ordenamiento() {
    }

    /**
     * Ordenamiento por inserción, sobre la misma lista. Es simple y eficiente para listas cortas, como
     * las pocas ofertas de proveedores que tiene un producto (complejidad O(n^2) en el peor caso).
     */
    public static <T> void ordenarPorInsercion(List<T> lista, Comparator<? super T> comparador) {
        for (int i = 1; i < lista.size(); i++) {
            T clave = lista.get(i);
            int j = i - 1;
            while (j >= 0 && comparador.compare(lista.get(j), clave) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, clave);
        }
    }

    /**
     * Ordenamiento por mezcla (merge sort): divide la lista en dos mitades, ordena cada una de forma
     * recursiva y las combina. Devuelve una lista nueva y no modifica la original. Complejidad
     * O(n log n), apropiada para el catálogo completo de productos.
     */
    public static <T> List<T> ordenarPorMezcla(List<T> lista, Comparator<? super T> comparador) {
        if (lista.size() <= 1) {
            return new ArrayList<>(lista);
        }
        int mitad = lista.size() / 2;
        List<T> izquierda = ordenarPorMezcla(lista.subList(0, mitad), comparador);
        List<T> derecha = ordenarPorMezcla(lista.subList(mitad, lista.size()), comparador);
        return mezclar(izquierda, derecha, comparador);
    }

    private static <T> List<T> mezclar(List<T> izquierda, List<T> derecha, Comparator<? super T> comparador) {
        List<T> resultado = new ArrayList<>(izquierda.size() + derecha.size());
        int i = 0;
        int d = 0;
        while (i < izquierda.size() && d < derecha.size()) {
            if (comparador.compare(izquierda.get(i), derecha.get(d)) <= 0) {
                resultado.add(izquierda.get(i++));
            } else {
                resultado.add(derecha.get(d++));
            }
        }
        while (i < izquierda.size()) {
            resultado.add(izquierda.get(i++));
        }
        while (d < derecha.size()) {
            resultado.add(derecha.get(d++));
        }
        return resultado;
    }
}
