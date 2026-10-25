package com.sabordelsur.inventario.archivos;

import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReporteCSVTest {

    private Path carpeta;

    @BeforeEach
    void crearCarpetaTemporal() throws Exception {
        carpeta = Files.createTempDirectory("tp4-prueba");
    }

    @Test
    void escapaSeparadorYComillas() {
        assertEquals("simple", ReporteCSV.escapar("simple"));
        assertEquals("\"a;b\"", ReporteCSV.escapar("a;b"));
        assertEquals("\"dijo \"\"hola\"\"\"", ReporteCSV.escapar("dijo \"hola\""));
        assertEquals("", ReporteCSV.escapar(null));
    }

    @Test
    void exportaEncabezadoYUnaFilaPorProducto() throws Exception {
        Categoria c = new Categoria(1, "Aceites");
        List<Producto> productos = Arrays.asList(
                new Producto(1, "Aceite de girasol 5L", c, 12, 10),
                new Producto(2, "Aceite de oliva 1L", c, 4, 5));
        Path archivo = carpeta.resolve("sub").resolve("stock.csv");

        int filas = new ReporteStockCSV().exportar(productos, archivo);

        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        assertEquals(2, filas);
        assertEquals(3, lineas.size());
        assertTrue(lineas.get(0).startsWith("﻿ID;Producto;Categoría"));
        assertEquals("2;Aceite de oliva 1L;Aceites;4;5;Bajo el mínimo", lineas.get(2));
    }

    @Test
    void filaConCantidadDeValoresIncorrectaSeDetecta() {
        ReporteCSV<String> malFormado = new ReporteCSV<String>() {
            @Override
            protected String[] encabezados() {
                return new String[] {"A", "B"};
            }

            @Override
            protected String[] fila(String s) {
                return new String[] {s};
            }
        };
        assertThrows(IllegalStateException.class,
                () -> malFormado.exportar(Collections.singletonList("x"), carpeta.resolve("x.csv")));
    }
}
