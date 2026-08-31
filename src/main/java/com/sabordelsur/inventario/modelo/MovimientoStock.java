package com.sabordelsur.inventario.modelo;

import java.time.LocalDateTime;

/** Representa cada entrada o salida de stock registrada (UC-08), asociada a un Producto y al Usuario que la registró. */
public class MovimientoStock {

    private int id;
    private Producto producto;
    private Usuario usuario;
    private LocalDateTime fecha;
    private TipoMovimiento tipo;
    private int cantidad;

    public MovimientoStock() {
    }

    public MovimientoStock(Producto producto, Usuario usuario, TipoMovimiento tipo, int cantidad) {
        this.producto = producto;
        this.usuario = usuario;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
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

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** Variación de stock que corresponde aplicar: positiva si es ENTRADA, negativa si es SALIDA. */
    public int getVariacionStock() {
        return tipo == TipoMovimiento.ENTRADA ? cantidad : -cantidad;
    }
}
