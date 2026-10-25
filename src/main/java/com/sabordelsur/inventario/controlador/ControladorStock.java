package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.algoritmos.Busqueda;
import com.sabordelsur.inventario.algoritmos.Ordenamiento;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ResumenInventario;
import com.sabordelsur.inventario.persistencia.CrudDAO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** UC-11: consulta de stock, búsqueda por nombre, orden por criticidad y resumen del inventario. */
public class ControladorStock {

    private final CrudDAO<Producto> productoDAO;

    public ControladorStock(CrudDAO<Producto> productoDAO) {
        this.productoDAO = productoDAO;
    }

    public List<Producto> listar() throws PersistenciaException {
        return productoDAO.listarTodos();
    }

    /** Devuelve el producto con ese ID o lanza ProductoNoEncontradoException si no existe. */
    public Producto buscarPorId(int idProducto) throws InventarioException {
        return productoDAO.buscarPorId(idProducto);
    }

    /**
     * Busca productos por nombre. Ordena el catálogo por nombre (ordenamiento por mezcla) y prueba una
     * coincidencia exacta con búsqueda binaria; si no la hay, devuelve las coincidencias parciales
     * obtenidas con búsqueda lineal. Con el texto vacío devuelve el catálogo completo.
     */
    public List<Producto> buscar(String texto) throws PersistenciaException {
        List<Producto> ordenados = Ordenamiento.ordenarPorMezcla(productoDAO.listarTodos(), Comparator.naturalOrder());
        if (texto == null || texto.trim().isEmpty()) {
            return ordenados;
        }
        int posicion = Busqueda.binariaPorNombre(ordenados, texto.trim());
        if (posicion >= 0) {
            return Collections.singletonList(ordenados.get(posicion));
        }
        return Busqueda.linealPorTexto(ordenados, texto.trim());
    }

    /** Devuelve una nueva lista con los productos del más crítico al menos crítico. */
    public List<Producto> ordenarPorCriticidad(List<Producto> productos) {
        return Ordenamiento.ordenarPorMezcla(productos, Comparator.comparingDouble(Producto::getNivelCriticidad));
    }

    public ResumenInventario resumir(List<Producto> productos) {
        return new ResumenInventario(productos);
    }
}
