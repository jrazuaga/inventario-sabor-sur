package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.algoritmos.Ordenamiento;
import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import com.sabordelsur.inventario.modelo.Proveedor;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.CrudDAO;
import com.sabordelsur.inventario.persistencia.ProductoProveedorDAO;

import java.util.Comparator;
import java.util.List;

/** UC-03 (gestión de proveedores), UC-04 (ofertas producto-proveedor) y UC-05 (comparación de ofertas). */
public class ControladorProveedores {

    /** Criterios de comparación de ofertas, en el mismo orden en que se muestran en la interfaz. */
    public static final String[] CRITERIOS = {"Menor precio", "Menor plazo de entrega"};

    private final CrudDAO<Proveedor> proveedorDAO;
    private final ProductoProveedorDAO ofertaDAO;
    private final Bitacora bitacora;

    public ControladorProveedores(CrudDAO<Proveedor> proveedorDAO, ProductoProveedorDAO ofertaDAO, Bitacora bitacora) {
        this.proveedorDAO = proveedorDAO;
        this.ofertaDAO = ofertaDAO;
        this.bitacora = bitacora;
    }

    public List<Proveedor> listar() throws PersistenciaException {
        return proveedorDAO.listarTodos();
    }

    public void guardar(Proveedor proveedor, Usuario actor) throws InventarioException {
        proveedor.validar();
        if (proveedor.getId() == 0) {
            proveedorDAO.insertar(proveedor);
            bitacora.registrar(actor.getNombreUsuario(), "Alta de proveedor: " + proveedor.getNombre());
        } else {
            proveedorDAO.actualizar(proveedor);
            bitacora.registrar(actor.getNombreUsuario(), "Modificación de proveedor #" + proveedor.getId()
                    + ": " + proveedor.getNombre());
        }
    }

    public void eliminar(Proveedor proveedor, Usuario actor) throws InventarioException {
        proveedorDAO.eliminar(proveedor.getId());
        bitacora.registrar(actor.getNombreUsuario(), "Baja de proveedor #" + proveedor.getId() + ": " + proveedor.getNombre());
    }

    /**
     * UC-05: ofertas de un producto ordenadas según el criterio elegido (índice del arreglo CRITERIOS),
     * con el algoritmo de ordenamiento por inserción.
     */
    public List<ProductoProveedor> compararOfertas(Producto producto, int criterio) throws PersistenciaException {
        List<ProductoProveedor> ofertas = ofertaDAO.listarPorProducto(producto);
        Comparator<ProductoProveedor> comparador = criterio == 1 ? ProductoProveedor.POR_PLAZO : ProductoProveedor.POR_PRECIO;
        Ordenamiento.ordenarPorInsercion(ofertas, comparador);
        return ofertas;
    }

    /** Devuelve la primera oferta disponible de una lista ya ordenada, o null si ninguna está disponible. */
    public ProductoProveedor mejorOferta(List<ProductoProveedor> ofertasOrdenadas) {
        for (ProductoProveedor oferta : ofertasOrdenadas) {
            if (oferta.isDisponible()) {
                return oferta;
            }
        }
        return null;
    }

    public List<ProductoProveedor> ofertasDeProveedor(Proveedor proveedor) throws PersistenciaException {
        return ofertaDAO.listarPorProveedor(proveedor.getId());
    }

    public void guardarOferta(ProductoProveedor oferta, Usuario actor) throws InventarioException {
        oferta.validar();
        ofertaDAO.guardar(oferta);
        bitacora.registrar(actor.getNombreUsuario(), String.format("Oferta guardada: %s / %s ($ %.2f, %d días)",
                oferta.getProveedor().getNombre(), oferta.getProducto().getNombre(), oferta.getPrecio(),
                oferta.getPlazoEntregaDias()));
    }

    public void quitarOferta(ProductoProveedor oferta, Usuario actor) throws InventarioException {
        ofertaDAO.eliminar(oferta.getProducto().getId(), oferta.getProveedor().getId());
        bitacora.registrar(actor.getNombreUsuario(), "Oferta quitada: " + oferta.getProveedor().getNombre()
                + " / " + oferta.getProducto().getNombre());
    }
}
