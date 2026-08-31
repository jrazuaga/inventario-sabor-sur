package com.sabordelsur.inventario.modelo;

/**
 * Clase asociativa entre Producto y Proveedor: representa la condición particular (precio, disponibilidad)
 * que un proveedor determinado ofrece para un producto determinado (UC-04, UC-05).
 */
public class ProductoProveedor {

    private Producto producto;
    private Proveedor proveedor;
    private double precio;
    private boolean disponible;

    public ProductoProveedor() {
    }

    public ProductoProveedor(Producto producto, Proveedor proveedor, double precio, boolean disponible) {
        this.producto = producto;
        this.proveedor = proveedor;
        this.precio = precio;
        this.disponible = disponible;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
