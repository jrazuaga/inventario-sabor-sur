package com.sabordelsur.inventario.modelo;

/**
 * Representa cada ítem del catálogo de la distribuidora (ej.: un tipo de aceite, harina o descartable).
 * Concentra la categoría a la que pertenece y el nivel de stock actual frente al umbral mínimo definido.
 * Implementa Comparable para que pueda ordenarse y buscarse por nombre (ver paquete algoritmos).
 */
public class Producto implements Comparable<Producto> {

    private int id;
    private String nombre;
    private Categoria categoria;
    private int stockActual;
    private int stockMinimo;

    public Producto() {
    }

    public Producto(int id, String nombre, Categoria categoria, int stockActual, int stockMinimo) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    /** UC-11: Consultar stock de productos. */
    public int consultarStock() {
        return stockActual;
    }

    /**
     * Aplica una variación de stock (positiva para ENTRADA, negativa para SALIDA) sobre el stock actual.
     * La persistencia y el disparo de los pedidos de reposición (UC-09/UC-10) se resuelven en la capa DAO.
     */
    public void actualizarStock(int cantidad) {
        this.stockActual += cantidad;
    }

    /** Indica si, con el stock actual, corresponde generar un pedido de reposición (UC-09). */
    public boolean estaPorDebajoDelMinimo() {
        return stockActual < stockMinimo;
    }

    /** Indica si hay unidades suficientes para despachar la cantidad pedida. */
    public boolean puedeDespachar(int cantidad) {
        return stockActual >= cantidad;
    }

    /**
     * Relación entre el stock actual y el mínimo: un valor menor a 1 indica que el producto está
     * por debajo del umbral, y cuanto más bajo, más crítica es la situación.
     */
    public double getNivelCriticidad() {
        if (stockMinimo <= 0) {
            return Double.POSITIVE_INFINITY;
        }
        return (double) stockActual / stockMinimo;
    }

    /** Orden natural: alfabético por nombre, sin distinguir mayúsculas de minúsculas. */
    @Override
    public int compareTo(Producto otro) {
        return String.CASE_INSENSITIVE_ORDER.compare(nombre, otro.nombre);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    @Override
    public String toString() {
        return String.format("%s (stock: %d/%d)", nombre, stockActual, stockMinimo);
    }
}
