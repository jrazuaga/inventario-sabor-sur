package com.sabordelsur.inventario.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola (FIFO) implementada con nodos enlazados: el primero que entra es el primero que sale. Se usa
 * para representar la cola de pedidos de reposición, que según la regla de negocio se atienden en el
 * orden en que fueron generados.
 */
public class ColaEnlazada<T> implements Iterable<T> {

    private static class Nodo<T> {
        private final T dato;
        private Nodo<T> siguiente;

        private Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    /** Agrega un elemento al final de la cola. */
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamanio++;
    }

    /** Quita y devuelve el elemento del frente. Lanza NoSuchElementException si la cola está vacía. */
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola está vacía.");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /** Devuelve el elemento del frente sin quitarlo. Lanza NoSuchElementException si la cola está vacía. */
    public T verFrente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola está vacía.");
        }
        return frente.dato;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Recorre la cola desde el frente hasta el final, sin modificarla. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

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
