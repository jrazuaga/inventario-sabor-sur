package com.sabordelsur.inventario.archivos;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Lee los datos de conexión desde el archivo config/sabor_sur.properties, para no tener que modificar
 * el código en cada equipo. El orden de prioridad es: variable de entorno, archivo de configuración y,
 * por último, el valor por defecto.
 */
public final class ConfiguracionBD {

    public static final Path ARCHIVO_POR_DEFECTO = Paths.get("config", "sabor_sur.properties");

    private final Properties propiedades;

    public ConfiguracionBD(Properties propiedades) {
        this.propiedades = propiedades;
    }

    /**
     * Carga el archivo indicado. Si no existe o no se puede leer, devuelve una configuración vacía: la
     * aplicación sigue funcionando con las variables de entorno o los valores por defecto.
     */
    public static ConfiguracionBD cargar(Path archivo) {
        Properties propiedades = new Properties();
        if (Files.isReadable(archivo)) {
            try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
                propiedades.load(lector);
            } catch (IOException e) {
                System.err.println("Aviso: no se pudo leer " + archivo + " (" + e.getMessage() + ").");
            }
        }
        return new ConfiguracionBD(propiedades);
    }

    /** Valor de una clave: primero la variable de entorno, después el archivo, después el valor por defecto. */
    public String obtener(String variableEntorno, String clave, String valorPorDefecto) {
        String valor = System.getenv(variableEntorno);
        if (valor == null || valor.isEmpty()) {
            valor = propiedades.getProperty(clave);
        }
        return (valor == null || valor.trim().isEmpty()) ? valorPorDefecto : valor.trim();
    }
}
