package com.sabordelsur.inventario.modelo;

/** Clasificación del stock de un producto respecto de su umbral mínimo. */
public enum EstadoStock {
    SIN_STOCK("Sin stock"),
    BAJO_MINIMO("Bajo el mínimo"),
    NORMAL("Normal");

    private final String descripcion;

    EstadoStock(String descripcion) {
        this.descripcion = descripcion;
    }

    public static EstadoStock de(int stockActual, int stockMinimo) {
        if (stockActual <= 0) {
            return SIN_STOCK;
        }
        return stockActual < stockMinimo ? BAJO_MINIMO : NORMAL;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
