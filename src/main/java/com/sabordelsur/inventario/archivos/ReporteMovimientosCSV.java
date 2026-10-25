package com.sabordelsur.inventario.archivos;

import com.sabordelsur.inventario.modelo.MovimientoStock;

import java.time.format.DateTimeFormatter;

/** Reporte de los movimientos de stock registrados, con fecha, tipo, cantidad y usuario (trazabilidad). */
public class ReporteMovimientosCSV extends ReporteCSV<MovimientoStock> {

    private static final String[] COLUMNAS = {"ID", "Fecha", "Tipo", "Producto", "Cantidad", "Usuario"};
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected String[] encabezados() {
        return COLUMNAS.clone();
    }

    @Override
    protected String[] fila(MovimientoStock m) {
        return new String[] {
                String.valueOf(m.getId()),
                m.getFecha().format(FORMATO),
                m.getTipo().name(),
                m.getProducto().getNombre(),
                String.valueOf(m.getCantidad()),
                m.getUsuario().getNombreUsuario()
        };
    }
}
