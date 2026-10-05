package com.sabordelsur.inventario.excepciones;

/** Se lanza cuando una salida pide más unidades de las que hay disponibles en el depósito. */
public class StockInsuficienteException extends InventarioException {

    private static final long serialVersionUID = 1L;

    private final int disponible;
    private final int solicitado;

    public StockInsuficienteException(String producto, int disponible, int solicitado) {
        super(String.format("Stock insuficiente de \"%s\": hay %d unidades y se solicitaron %d.",
                producto, disponible, solicitado));
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public int getDisponible() {
        return disponible;
    }

    public int getSolicitado() {
        return solicitado;
    }
}
