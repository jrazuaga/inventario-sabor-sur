package com.sabordelsur.inventario.archivos;

import com.sabordelsur.inventario.excepciones.ArchivoException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bitácora de actividad en un archivo de texto (logs/bitacora.log). Cada línea registra fecha y hora,
 * usuario y acción: inicios de sesión, movimientos de stock, altas, bajas y exportaciones. Complementa
 * la trazabilidad de la base con un registro que se puede abrir con cualquier editor.
 */
public class Bitacora {

    public static final Path ARCHIVO_POR_DEFECTO = Paths.get("logs", "bitacora.log");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path archivo;

    public Bitacora(Path archivo) {
        this.archivo = archivo;
    }

    public Bitacora() {
        this(ARCHIVO_POR_DEFECTO);
    }

    /**
     * Agrega una línea al final del archivo (lo crea si no existe). Si el disco no permite escribir, se
     * avisa por la salida de error pero no se interrumpe la operación del usuario: la bitácora es un
     * registro complementario y la información principal ya quedó guardada en MySQL.
     */
    public void registrar(String usuario, String accion) {
        String linea = String.format("%s | %-12s | %s%n", LocalDateTime.now().format(FORMATO), usuario, accion);
        try {
            Path carpeta = archivo.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.write(archivo, linea.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Aviso: no se pudo escribir en la bitácora (" + e.getMessage() + ").");
        }
    }

    /** Recupera las últimas n líneas, de la más reciente a la más antigua. */
    public List<String> leerUltimas(int n) throws ArchivoException {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            List<String> ultimas = new ArrayList<>(lineas.subList(Math.max(0, lineas.size() - n), lineas.size()));
            Collections.reverse(ultimas);
            return ultimas;
        } catch (IOException e) {
            throw new ArchivoException("No se pudo leer la bitácora " + archivo + ".", e);
        }
    }

    public Path getArchivo() {
        return archivo;
    }
}
