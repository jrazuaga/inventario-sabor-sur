package com.sabordelsur.inventario.archivos;

import com.sabordelsur.inventario.modelo.Producto;

/** Reporte del stock actual de todos los productos, con su estado respecto del mínimo (UC-11). */
public class ReporteStockCSV extends ReporteCSV<Producto> {

    private static final String[] COLUMNAS = {"ID", "Producto", "Categoría", "Stock actual", "Stock mínimo", "Estado"};

    @Override
    protected String[] encabezados() {
        return COLUMNAS.clone();
    }

    @Override
    protected String[] fila(Producto p) {
        return new String[] {
                String.valueOf(p.getId()),
                p.getNombre(),
                p.getCategoria() != null ? p.getCategoria().getNombre() : "",
                String.valueOf(p.getStockActual()),
                String.valueOf(p.getStockMinimo()),
                p.getEstadoStock().getDescripcion()
        };
    }
}
