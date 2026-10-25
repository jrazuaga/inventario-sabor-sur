package com.sabordelsur.inventario.vista;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo genérico para las tablas de la interfaz. Los títulos de las columnas se guardan en un arreglo,
 * porque son fijos para cada tabla, y las filas en un ArrayList, porque su cantidad cambia cada vez que
 * se consulta la base. Cada tabla concreta solo indica qué valor mostrar en cada columna.
 *
 * @param <T> tipo de objeto que representa cada fila
 */
public abstract class ModeloTabla<T> extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final String[] columnas;
    private final List<T> filas = new ArrayList<>();

    protected ModeloTabla(String... columnas) {
        this.columnas = columnas;
    }

    /** Valor que se muestra en la columna indicada para el objeto de la fila. */
    protected abstract Object valor(T fila, int columna);

    public void setFilas(List<T> nuevasFilas) {
        filas.clear();
        filas.addAll(nuevasFilas);
        fireTableDataChanged();
    }

    public T getFila(int indice) {
        return filas.get(indice);
    }

    public List<T> getFilas() {
        return new ArrayList<>(filas);
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }

    /**
     * Informa a la JTable el tipo de dato de la columna (tomado de la primera fila), para que los
     * números se alineen a la derecha y los importes usen el formato de moneda.
     */
    @Override
    public Class<?> getColumnClass(int columna) {
        if (filas.isEmpty()) {
            return Object.class;
        }
        Object valor = getValueAt(0, columna);
        return valor == null ? Object.class : valor.getClass();
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return valor(filas.get(fila), columna);
    }
}
