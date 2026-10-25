package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.excepciones.DatoInvalidoException;

import java.util.Comparator;

/**
 * Clase asociativa entre Producto y Proveedor: representa la condición particular (precio, plazo de
 * entrega, disponibilidad) que un proveedor determinado ofrece para un producto determinado (UC-04, UC-05).
 */
public class ProductoProveedor implements Validable {

    /** Criterios de ordenamiento para comparar ofertas (UC-05). */
    public static final Comparator<ProductoProveedor> POR_PRECIO =
            Comparator.comparingDouble(ProductoProveedor::getPrecio);
    public static final Comparator<ProductoProveedor> POR_PLAZO =
            Comparator.comparingInt(ProductoProveedor::getPlazoEntregaDias);

    private Producto producto;
    private Proveedor proveedor;
    private double precio;
    private int plazoEntregaDias;
    private boolean disponible;

    public ProductoProveedor() {
    }

    public ProductoProveedor(Producto producto, Proveedor proveedor, double precio,
                             int plazoEntregaDias, boolean disponible) {
        this.producto = producto;
        this.proveedor = proveedor;
        this.precio = precio;
        this.plazoEntregaDias = plazoEntregaDias;
        this.disponible = disponible;
    }

    /** UC-04: una oferta necesita producto, proveedor, un precio positivo y un plazo no negativo. */
    @Override
    public void validar() throws DatoInvalidoException {
        if (producto == null || proveedor == null) {
            throw new DatoInvalidoException("La oferta debe indicar el producto y el proveedor.");
        }
        if (precio <= 0) {
            throw new DatoInvalidoException("El precio debe ser mayor que cero.");
        }
        if (plazoEntregaDias < 0) {
            throw new DatoInvalidoException("El plazo de entrega no puede ser negativo.");
        }
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

    public int getPlazoEntregaDias() {
        return plazoEntregaDias;
    }

    public void setPlazoEntregaDias(int plazoEntregaDias) {
        this.plazoEntregaDias = plazoEntregaDias;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
