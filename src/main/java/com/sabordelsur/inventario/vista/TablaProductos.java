package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.modelo.Producto;

/** Modelo de tabla para listar productos con su stock y estado. */
public class TablaProductos extends ModeloTabla<Producto> {

    private static final long serialVersionUID = 1L;

    public TablaProductos() {
        super("ID", "Producto", "Categoría", "Stock actual", "Stock mínimo", "Estado");
    }

    @Override
    protected Object valor(Producto p, int columna) {
        switch (columna) {
            case 0:
                return p.getId();
            case 1:
                return p.getNombre();
            case 2:
                return p.getCategoria();
            case 3:
                return p.getStockActual();
            case 4:
                return p.getStockMinimo();
            default:
                return p.getEstadoStock();
        }
    }
}
