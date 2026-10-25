package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import com.sabordelsur.inventario.modelo.Proveedor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Acceso a datos (JDBC) para la tabla producto_proveedor, la clase asociativa del modelo de diseño (UC-04, UC-05). */
public class ProductoProveedorDAO extends DAOBase<ProductoProveedor> {

    private static final String SELECT_BASE =
            "SELECT p.id_producto, p.nombre AS nombre_producto, p.stock_actual, p.stock_minimo, " +
            "       c.id_categoria, c.nombre AS nombre_categoria, " +
            "       pv.id_proveedor, pv.nombre AS nombre_proveedor, pv.contacto, " +
            "       pp.precio, pp.plazo_entrega_dias, pp.disponible " +
            "FROM producto_proveedor pp " +
            "JOIN producto p   ON p.id_producto   = pp.id_producto " +
            "JOIN categoria c  ON c.id_categoria  = p.id_categoria " +
            "JOIN proveedor pv ON pv.id_proveedor = pp.id_proveedor ";

    @Override
    protected String getNombreEntidad() {
        return "oferta de proveedor";
    }

    @Override
    protected ProductoProveedor mapear(ResultSet rs) throws SQLException {
        Producto producto = new Producto(rs.getInt("id_producto"), rs.getString("nombre_producto"),
                new Categoria(rs.getInt("id_categoria"), rs.getString("nombre_categoria")),
                rs.getInt("stock_actual"), rs.getInt("stock_minimo"));
        Proveedor proveedor = new Proveedor(rs.getInt("id_proveedor"), rs.getString("nombre_proveedor"),
                rs.getString("contacto"));
        return new ProductoProveedor(producto, proveedor, rs.getDouble("precio"),
                rs.getInt("plazo_entrega_dias"), rs.getBoolean("disponible"));
    }

    /**
     * UC-05: devuelve las ofertas de los proveedores asociados a un producto. El orden lo decide quien
     * llama, aplicando los algoritmos del paquete algoritmos según el criterio de comparación elegido.
     */
    public List<ProductoProveedor> listarPorProducto(Producto producto) throws PersistenciaException {
        return consultarLista(SELECT_BASE + "WHERE pp.id_producto = ?", producto.getId());
    }

    /** UC-04: ofertas cargadas para un proveedor, ordenadas por nombre de producto. */
    public List<ProductoProveedor> listarPorProveedor(int idProveedor) throws PersistenciaException {
        return consultarLista(SELECT_BASE + "WHERE pp.id_proveedor = ? ORDER BY p.nombre", idProveedor);
    }

    /**
     * UC-04: registra o actualiza la condición que un proveedor ofrece para un producto. Como la clave
     * primaria es el par (producto, proveedor), se usa INSERT ... ON DUPLICATE KEY UPDATE: si la oferta
     * ya existía, se reemplazan precio, plazo y disponibilidad.
     */
    public void guardar(ProductoProveedor oferta) throws InventarioException {
        oferta.validar();
        ejecutarActualizacion("guardar la oferta",
                "INSERT INTO producto_proveedor (id_producto, id_proveedor, precio, plazo_entrega_dias, disponible) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE precio = VALUES(precio), plazo_entrega_dias = VALUES(plazo_entrega_dias), " +
                "disponible = VALUES(disponible)",
                oferta.getProducto().getId(), oferta.getProveedor().getId(), oferta.getPrecio(),
                oferta.getPlazoEntregaDias(), oferta.isDisponible());
    }

    /** UC-04: quita la oferta de un proveedor para un producto. */
    public void eliminar(int idProducto, int idProveedor) throws InventarioException {
        ejecutarActualizacion("quitar la oferta",
                "DELETE FROM producto_proveedor WHERE id_producto = ? AND id_proveedor = ?", idProducto, idProveedor);
    }
}
