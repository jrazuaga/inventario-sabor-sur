package com.sabordelsur.inventario.modelo;

/**
 * Representa cada ítem del catálogo de la distribuidora (ej.: un tipo de aceite, harina o descartable).
 * Concentra la categoría a la que pertenece y el nivel de stock actual frente al umbral mínimo definido.
 */
public class Producto {

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
     * La persistencia y el disparo de los pedidos de reposición (UC-09/UC-10) se resuelven en la capa DAO,
     * ya que requieren acceso a la base de datos.
     */
    public void actualizarStock(int cantidad) {
        this.stockActual += cantidad;
    }

    /** Indica si, con el stock actual, corresponde generar un pedido de reposición (UC-09). */
    public boolean estaPorDebajoDelMinimo() {
        return stockActual < stockMinimo;
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
