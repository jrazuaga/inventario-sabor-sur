package com.sabordelsur.inventario.archivos;

import com.sabordelsur.inventario.excepciones.ArchivoException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Clase abstracta para exportar listados a archivos CSV que se abren directamente con Excel o
 * LibreOffice. Sigue el patrón método plantilla: exportar() fija los pasos (crear la carpeta, escribir
 * el encabezado y recorrer los datos) y cada reporte concreto define solo sus columnas y cómo se
 * convierte cada objeto en una fila.
 *
 * Las columnas y cada fila se representan con arreglos de String, porque su tamaño es fijo y conocido;
 * los datos llegan en una lista, porque su cantidad depende de lo que haya en la base.
 *
 * @param <T> tipo de objeto que se exporta
 */
public abstract class ReporteCSV<T> {

    /** Punto y coma: es el separador que Excel espera con la configuración regional de Argentina. */
    public static final char SEPARADOR = ';';
    /** Marca de orden de bytes: le indica a Excel que el archivo está en UTF-8 (para las tildes y la ñ). */
    private static final String BOM = "﻿";

    /** Títulos de las columnas, en orden. */
    protected abstract String[] encabezados();

    /** Valores de una fila; debe tener la misma cantidad de elementos que encabezados(). */
    protected abstract String[] fila(T elemento);

    /** Escribe el archivo y devuelve la cantidad de filas de datos exportadas. */
    public final int exportar(List<T> datos, Path destino) throws ArchivoException {
        String[] columnas = encabezados();
        try {
            Path carpeta = destino.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            try (BufferedWriter salida = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
                salida.write(BOM);
                salida.write(unir(columnas));
                salida.newLine();
                for (T elemento : datos) {
                    String[] valores = fila(elemento);
                    if (valores.length != columnas.length) {
                        throw new IllegalStateException("La fila tiene " + valores.length
                                + " valores y se esperaban " + columnas.length + ".");
                    }
                    salida.write(unir(valores));
                    salida.newLine();
                }
            }
            return datos.size();
        } catch (IOException e) {
            throw new ArchivoException("No se pudo generar el archivo " + destino + ".", e);
        }
    }

    /** Une los valores con el separador, escapando los que lo necesitan. */
    static String unir(String[] valores) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                linea.append(SEPARADOR);
            }
            linea.append(escapar(valores[i]));
        }
        return linea.toString();
    }

    /**
     * Si el valor contiene el separador, comillas o saltos de línea, lo encierra entre comillas y
     * duplica las comillas internas, como indica el formato CSV (RFC 4180).
     */
    static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        boolean requiereComillas = valor.indexOf(SEPARADOR) >= 0 || valor.indexOf('"') >= 0
                || valor.indexOf('\n') >= 0 || valor.indexOf('\r') >= 0;
        return requiereComillas ? '"' + valor.replace("\"", "\"\"") + '"' : valor;
    }
}
