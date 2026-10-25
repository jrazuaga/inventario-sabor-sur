package com.sabordelsur.inventario.archivos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfiguracionBDTest {

    private Path carpeta;

    @BeforeEach
    void crearCarpetaTemporal() throws Exception {
        carpeta = Files.createTempDirectory("tp4-prueba");
    }

    @Test
    void leeLosValoresDelArchivo() throws Exception {
        Path archivo = carpeta.resolve("sabor_sur.properties");
        Files.write(archivo, Arrays.asList("db.usuario=inventario", "db.url=jdbc:mysql://servidor:3306/sabor_sur_db"),
                StandardCharsets.UTF_8);
        ConfiguracionBD config = ConfiguracionBD.cargar(archivo);
        assertEquals("inventario", config.obtener("VARIABLE_INEXISTENTE_TP4", "db.usuario", "root"));
    }

    @Test
    void sinArchivoUsaElValorPorDefecto() {
        ConfiguracionBD config = ConfiguracionBD.cargar(carpeta.resolve("no-existe.properties"));
        assertEquals("root", config.obtener("VARIABLE_INEXISTENTE_TP4", "db.usuario", "root"));
    }
}
