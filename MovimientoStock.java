package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.excepciones.CantidadInvalidaException;
import com.sabordelsur.inventario.excepciones.InventarioException;

import java.time.LocalDateTime;

/**
 * Representa cada entrada o salida de stock registrada (UC-08), asociada a un Producto y al Usuario
 * que la registró. Es abstracta: cada tipo de movimiento (entrada o salida) define su efecto sobre el
 * stock y su regla propia de validación. La validación completa sigue el patrón "método plantilla":
 * validar() fija los pasos y las subclases completan el paso específico.
 */
public abstract class MovimientoStock {

    private int id;
    private final Producto producto;
    private final Usuario usuario;
    private final int cantidad;
    private LocalDateTime fecha;

    protected MovimientoStock(Producto producto, Usuario usuario, int cantidad) {
        this.producto = producto;
        this.usuario = usuario;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
    }

    /** Método de fábrica: devuelve la subclase que corresponde al tipo de movimiento pedido. */
    public static MovimientoStock crear(TipoMovimiento tipo, Producto producto, Usuario usuario, int cantidad) {
        switch (tipo) {
            case ENTRADA:
                return new MovimientoEntrada(producto, usuario, cantidad);
            case SALIDA:
                return new MovimientoSalida(producto, usuario, cantidad);
            default:
                throw new IllegalArgumentException("Tipo de movimiento no soportado: " + tipo);
        }
    }

    /** Tipo de movimiento. Lo define cada subclase. */
    public abstract TipoMovimiento getTipo();

    /** Variación que corresponde aplicar al stock: positiva si es entrada, negativa si es salida. */
    public abstract int getVariacionStock();

    /** Regla de validación propia de cada tipo de movimiento. */
    protected abstract void validarReglaPropia() throws InventarioException;

    /** Valida el movimiento: primero la cantidad (común a todos) y luego la regla propia del tipo. */
    public final void validar() throws InventarioException {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException(cantidad);
        }
        validarReglaPropia();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
