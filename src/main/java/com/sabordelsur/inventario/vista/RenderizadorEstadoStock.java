package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.modelo.EstadoStock;
import com.sabordelsur.inventario.modelo.Producto;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/** Pinta cada fila según el estado del stock: rojo si no hay stock, ámbar si está bajo el mínimo. */
public class RenderizadorEstadoStock extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private final ModeloTabla<Producto> modelo;

    public RenderizadorEstadoStock(ModeloTabla<Producto> modelo) {
        this.modelo = modelo;
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean foco, int fila, int columna) {
        Component celda = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
        setHorizontalAlignment(valor instanceof Number ? RIGHT : LEFT);
        if (!seleccionada) {
            EstadoStock estado = modelo.getFila(tabla.convertRowIndexToModel(fila)).getEstadoStock();
            celda.setBackground(estado == EstadoStock.SIN_STOCK ? Estilo.ROJO_SUAVE
                    : estado == EstadoStock.BAJO_MINIMO ? Estilo.AMBAR_SUAVE : Color.WHITE);
            celda.setForeground(Color.BLACK);
        }
        return celda;
    }
}
