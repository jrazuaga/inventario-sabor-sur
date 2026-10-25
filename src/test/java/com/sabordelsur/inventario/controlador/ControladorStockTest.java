package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControladorStockTest {

    private ControladorStock controlador;

    @BeforeEach
    void preparar() {
        RepositorioEnMemoria<Producto> repo = new RepositorioEnMemoria<>(Producto::getId, Producto::setId);
        Categoria c = new Categoria(1, "Varios");
        repo.insertar(new Producto(0, "Harina 0000 x25kg", c, 20, 15));
        repo.insertar(new Producto(0, "Aceite de oliva 1L", c, 4, 5));
        repo.insertar(new Producto(0, "Harina integral x25kg", c, 8, 10));
        controlador = new ControladorStock(repo);
    }

    @Test
    void coincidenciaExactaDevuelveUnSoloProducto() throws Exception {
        List<Producto> r = controlador.buscar("aceite de oliva 1l");
        assertEquals(1, r.size());
        assertEquals("Aceite de oliva 1L", r.get(0).getNombre());
    }

    @Test
    void coincidenciaParcialDevuelveTodosLosQueContienenElTexto() throws Exception {
        assertEquals(2, controlador.buscar("harina").size());
    }

    @Test
    void textoVacioDevuelveElCatalogoOrdenadoPorNombre() throws Exception {
        List<Producto> r = controlador.buscar("");
        assertEquals(3, r.size());
        assertEquals("Aceite de oliva 1L", r.get(0).getNombre());
    }

    @Test
    void ordenPorCriticidadPoneAlMasCriticoPrimero() throws Exception {
        List<Producto> r = controlador.ordenarPorCriticidad(controlador.listar());
        assertEquals("Aceite de oliva 1L", r.get(0).getNombre());   // 4/5 = 0,8
        assertEquals("Harina 0000 x25kg", r.get(2).getNombre());    // 20/15
    }
}
