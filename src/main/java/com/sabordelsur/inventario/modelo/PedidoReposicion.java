package com.sabordelsur.inventario.modelo;

import java.time.LocalDateTime;

/**
 * Representa un pedido generado automáticamente cuando el stock de un Producto cae por debajo
 * de su umbral mínimo (UC-09), y que se retira de la cola cuando el stock se recompone (UC-10).
 */
public class PedidoReposicion {

    private int id;
    private Producto producto;
    private LocalDateTime fechaGeneracion;
    private EstadoPedido estado;

    public PedidoReposicion() {
    }

    public PedidoReposicion(Producto producto) {
        this.producto = producto;
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
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

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("Pedido #%d - %s - %s", id, producto != null ? producto.getNombre() : "?", estado);
    }
}
