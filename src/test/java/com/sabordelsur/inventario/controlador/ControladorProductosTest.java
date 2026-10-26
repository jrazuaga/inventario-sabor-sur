package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Administrador;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ControladorProductosTest {


    private RepositorioEnMemoria<Producto> productos;
    private RepositorioEnMemoria<Categoria> categorias;
    private ControladorProductos controlador;
    private final Usuario actor = new Administrador(1, "propietario", null);
    private final Categoria aceites = new Categoria(1, "Aceites");

    @BeforeEach
    void preparar() throws Exception {
        Path carpeta = Files.createTempDirectory("tp4-prueba");
        productos = new RepositorioEnMemoria<>(Producto::getId, Producto::setId);
        categorias = new RepositorioEnMemoria<>(Categoria::getId, Categoria::setId);
        controlador = new ControladorProductos(productos, categorias, new Bitacora(carpeta.resolve("b.log")));
    }

    @Test
    void altaAsignaIdYQuedaEnElListado() throws Exception {
        Producto nuevo = new Producto(0, "Aceite de maíz 1L", aceites, 10, 4);
        controlador.guardar(nuevo, actor);
        assertEquals(1, nuevo.getId());
        assertEquals(1, controlador.listar().size());
    }

    @Test
    void productoConIdSeActualizaEnLugarDeInsertarse() throws Exception {
        Producto existente = new Producto(0, "Aceite de maíz 1L", aceites, 10, 4);
        controlador.guardar(existente, actor);
        existente.setStockMinimo(8);
        controlador.guardar(existente, actor);
        assertEquals(1, productos.actualizaciones);
        assertEquals(1, controlador.listar().size());
    }

    @Test
    void nombreVacioSeRechazaSinLlegarAlRepositorio() {
        Producto invalido = new Producto(0, "   ", aceites, 10, 4);
        assertThrows(DatoInvalidoException.class, () -> controlador.guardar(invalido, actor));
        assertEquals(0, productos.listarTodos().size());
    }

    @Test
    void stockMinimoNegativoSeRechaza() {
        Producto invalido = new Producto(0, "Aceite de maíz 1L", aceites, 10, -1);
        assertThrows(DatoInvalidoException.class, () -> controlador.guardar(invalido, actor));
    }

    @Test
    void categoriaSinNombreSeRechaza() {
        assertThrows(DatoInvalidoException.class, () -> controlador.crearCategoria(" ", actor));
    }

    @Test
    void renombrarCategoriaActualizaElNombre() throws Exception {
        Categoria categoria = controlador.crearCategoria("Lacteos", actor);
        controlador.renombrarCategoria(categoria, "Lácteos", actor);
        assertEquals("Lácteos", categoria.getNombre());
        assertEquals(1, categorias.actualizaciones);
    }
}
