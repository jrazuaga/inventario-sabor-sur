package com.sabordelsur.inventario.modelo;

/** Resumen de lo ocurrido al registrar un movimiento: stock resultante y efecto sobre la cola de pedidos. */
public class ResultadoMovimiento {

    private final int stockResultante;
    private final boolean pedidoGenerado;
    private final boolean pedidoResuelto;

    public ResultadoMovimiento(int stockResultante, boolean pedidoGenerado, boolean pedidoResuelto) {
        this.stockResultante = stockResultante;
        this.pedidoGenerado = pedidoGenerado;
        this.pedidoResuelto = pedidoResuelto;
    }

    public int getStockResultante() {
        return stockResultante;
    }

    public boolean isPedidoGenerado() {
        return pedidoGenerado;
    }

    public boolean isPedidoResuelto() {
        return pedidoResuelto;
    }
}
