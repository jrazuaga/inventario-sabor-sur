package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.CrudDAO;

import java.util.List;

/**
 * UC-02: alta, modificación y baja de productos y alta de categorías. Recibe los DAO por su interfaz
 * (CrudDAO), por eso puede probarse con un repositorio en memoria sin depender de MySQL.
 */
public class ControladorProductos {

    private final CrudDAO<Producto> productoDAO;
    private final CrudDAO<Categoria> categoriaDAO;
    private final Bitacora bitacora;

    public ControladorProductos(CrudDAO<Producto> productoDAO, CrudDAO<Categoria> categoriaDAO, Bitacora bitacora) {
        this.productoDAO = productoDAO;
        this.categoriaDAO = categoriaDAO;
        this.bitacora = bitacora;
    }

    public List<Producto> listar() throws PersistenciaException {
        return productoDAO.listarTodos();
    }

    public List<Categoria> listarCategorias() throws PersistenciaException {
        return categoriaDAO.listarTodos();
    }

    /** Da de alta el producto si todavía no tiene ID; si ya lo tiene, guarda los cambios. */
    public void guardar(Producto producto, Usuario actor) throws InventarioException {
        producto.validar();
        if (producto.getId() == 0) {
            productoDAO.insertar(producto);
            bitacora.registrar(actor.getNombreUsuario(), "Alta de producto: " + producto.getNombre()
                    + " (stock inicial " + producto.getStockActual() + ", mínimo " + producto.getStockMinimo() + ")");
        } else {
            productoDAO.actualizar(producto);
            bitacora.registrar(actor.getNombreUsuario(), "Modificación de producto #" + producto.getId()
                    + ": " + producto.getNombre() + " (mínimo " + producto.getStockMinimo() + ")");
        }
    }

    public void eliminar(Producto producto, Usuario actor) throws InventarioException {
        productoDAO.eliminar(producto.getId());
        bitacora.registrar(actor.getNombreUsuario(), "Baja de producto #" + producto.getId() + ": " + producto.getNombre());
    }

    public Categoria crearCategoria(String nombre, Usuario actor) throws InventarioException {
        Categoria categoria = new Categoria(0, nombre == null ? null : nombre.trim());
        categoria.validar();
        categoriaDAO.insertar(categoria);
        bitacora.registrar(actor.getNombreUsuario(), "Alta de categoría: " + categoria.getNombre());
        return categoria;
    }
}
