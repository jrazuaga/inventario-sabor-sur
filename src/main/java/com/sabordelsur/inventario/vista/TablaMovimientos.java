package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.modelo.MovimientoStock;

import java.time.format.DateTimeFormatter;

/** Modelo de tabla para listar movimientos de stock. */
public class TablaMovimientos extends ModeloTabla<MovimientoStock> {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TablaMovimientos() {
        super("Fecha", "Tipo", "Producto", "Cantidad", "Usuario");
    }

    @Override
    protected Object valor(MovimientoStock m, int columna) {
        switch (columna) {
            case 0:
                return m.getFecha().format(FORMATO);
            case 1:
                return m.getTipo();
            case 2:
                return m.getProducto().getNombre();
            case 3:
                return m.getCantidad();
            default:
                return m.getUsuario().getNombreUsuario();
        }
    }
}
