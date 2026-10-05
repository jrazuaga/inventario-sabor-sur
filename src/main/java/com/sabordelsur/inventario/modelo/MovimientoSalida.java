package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.excepciones.StockInsuficienteException;

/** Egreso de mercadería del depósito: resta unidades al stock y no puede dejarlo en negativo. */
public class MovimientoSalida extends MovimientoStock {

    public MovimientoSalida(Producto producto, Usuario usuario, int cantidad) {
        super(producto, usuario, cantidad);
    }

    @Override
    public TipoMovimiento getTipo() {
        return TipoMovimiento.SALIDA;
    }

    @Override
    public int getVariacionStock() {
        return -getCantidad();
    }

    @Override
    protected void validarReglaPropia() throws StockInsuficienteException {
        if (!getProducto().puedeDespachar(getCantidad())) {
            throw new StockInsuficienteException(
                    getProducto().getNombre(), getProducto().getStockActual(), getCantidad());
        }
    }
}
