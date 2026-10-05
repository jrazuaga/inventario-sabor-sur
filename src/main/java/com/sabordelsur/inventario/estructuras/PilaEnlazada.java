package com.sabordelsur.inventario.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Pila (LIFO) implementada con nodos enlazados: el último que entra es el primero que sale. Se usa
 * para listar los movimientos registrados durante la sesión, del más reciente al más antiguo.
 */
public class PilaEnlazada<T> implements Iterable<T> {

    private static class Nodo<T> {
        private final T dato;
        private final Nodo<T> siguiente;

        private Nodo(T dato, Nodo<T> siguiente) {
            this.dato = dato;
            this.siguiente = siguiente;
        }
    }

    private Nodo<T> tope;
    private int tamanio;

    /** Coloca un elemento en el tope de la pila. */
    public void apilar(T dato) {
        tope = new Nodo<>(dato, tope);
        tamanio++;
    }

    /** Quita y devuelve el elemento del tope. Lanza NoSuchElementException si la pila está vacía. */
    public T desapilar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La pila está vacía.");
        }
        T dato = tope.dato;
        tope = tope.siguiente;
        tamanio--;
        return dato;
    }

    /** Devuelve el elemento del tope sin quitarlo. Lanza NoSuchElementException si la pila está vacía. */
    public T verTope() {
        if (estaVacia()) {
            throw new NoSuchElementException("La pila está vacía.");
        }
        return tope.dato;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Recorre la pila desde el tope hacia la base, sin modificarla. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = tope;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}
