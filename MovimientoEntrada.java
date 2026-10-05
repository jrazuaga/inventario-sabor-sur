package com.sabordelsur.inventario.modelo;

/** Ingreso de mercadería al depósito: suma unidades al stock. */
public class MovimientoEntrada extends MovimientoStock {

    public MovimientoEntrada(Producto producto, Usuario usuario, int cantidad) {
        super(producto, usuario, cantidad);
    }

    @Override
    public TipoMovimiento getTipo() {
        return TipoMovimiento.ENTRADA;
    }

    @Override
    public int getVariacionStock() {
        return getCantidad();
    }

    @Override
    protected void validarReglaPropia() {
        // Una entrada no tiene restricciones adicionales: siempre se puede recibir mercadería.
    }
}
