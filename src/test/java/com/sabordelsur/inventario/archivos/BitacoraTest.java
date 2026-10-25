package com.sabordelsur.inventario.archivos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitacoraTest {

    private Path carpeta;

    @BeforeEach
    void crearCarpetaTemporal() throws Exception {
        carpeta = Files.createTempDirectory("tp4-prueba");
    }

    @Test
    void registraYRecuperaDeLaMasRecienteALaMasAntigua() throws Exception {
        Bitacora bitacora = new Bitacora(carpeta.resolve("logs").resolve("bitacora.log"));
        bitacora.registrar("operario1", "primera");
        bitacora.registrar("operario1", "segunda");
        bitacora.registrar("propietario", "tercera");

        List<String> ultimas = bitacora.leerUltimas(2);

        assertEquals(2, ultimas.size());
        assertTrue(ultimas.get(0).endsWith("| tercera"));
        assertTrue(ultimas.get(1).contains("operario1") && ultimas.get(1).endsWith("| segunda"));
    }

    @Test
    void archivoInexistenteDevuelveListaVacia() throws Exception {
        assertTrue(new Bitacora(carpeta.resolve("no-existe.log")).leerUltimas(10).isEmpty());
    }
}
