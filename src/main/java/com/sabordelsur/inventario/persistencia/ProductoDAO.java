package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.excepciones.ProductoNoEncontradoException;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Acceso a datos (JDBC) para la tabla producto, con el join a categoria ya resuelto. Implementa el
 * contrato CrudDAO (UC-02) y agrega las operaciones que usa el registro de movimientos (UC-08).
 */
public class ProductoDAO extends DAOBase<Producto> implements CrudDAO<Producto> {

    private static final String SELECT_BASE =
            "SELECT p.id_producto, p.nombre, p.stock_actual, p.stock_minimo, " +
            "       c.id_categoria, c.nombre AS nombre_categoria " +
            "FROM producto p JOIN categoria c ON c.id_categoria = p.id_categoria ";

    private final PedidoReposicionDAO pedidoDAO = new PedidoReposicionDAO();

    @Override
    protected String getNombreEntidad() {
        return "producto";
    }

    @Override
    protected Producto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("nombre_categoria"));
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                categoria,
                rs.getInt("stock_actual"),
                rs.getInt("stock_minimo"));
    }

    /** UC-11: listar el stock de todos los productos, agrupado por categoría. */
    @Override
    public List<Producto> listarTodos() throws PersistenciaException {
        return consultarLista(SELECT_BASE + "ORDER BY c.nombre, p.nombre");
    }

    @Override
    public Producto buscarPorId(int idProducto) throws PersistenciaException, ProductoNoEncontradoException {
        Producto producto = consultarUno(SELECT_BASE + "WHERE p.id_producto = ?", idProducto);
        if (producto == null) {
            throw new ProductoNoEncontradoException(idProducto);
        }
        return producto;
    }

    /**
     * UC-02 (alta): inserta el producto con su stock inicial y, en la misma transacción, aplica la regla
     * de reposición: si el stock inicial ya está por debajo del mínimo, se genera el pedido (UC-09).
     */
    @Override
    public int insertar(Producto producto) throws InventarioException {
        producto.validar();
        int id = ejecutarEnTransaccion("dar de alta el producto", con -> {
            int nuevoId = insertarYObtenerId(con,
                    "INSERT INTO producto (nombre, id_categoria, stock_actual, stock_minimo) "
                            + "VALUES (?, ?, ?, ?)",
                    producto.getNombre().trim(), producto.getCategoria().getId(),
                    producto.getStockActual(), producto.getStockMinimo());
            pedidoDAO.evaluar(con, nuevoId, producto.getStockActual(), producto.getStockMinimo());
            return nuevoId;
        });
        producto.setId(id);
        return id;
    }

    /**
     * UC-02 (modificación): cambia nombre, categoría y stock mínimo. El stock actual no se edita aquí:
     * solo cambia por medio de movimientos (UC-08), para que quede trazabilidad de cada unidad. Como el
     * nuevo mínimo puede dejar al producto por encima o por debajo del umbral, se vuelve a evaluar la
     * regla de reposición dentro de la misma transacción.
     */
    @Override
    public void actualizar(Producto producto) throws InventarioException {
        producto.validar();
        ejecutarEnTransaccion("modificar el producto", con -> {
            Producto actual = buscarPorIdBloqueando(con, producto.getId());
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE producto SET nombre = ?, id_categoria = ?, stock_minimo = ? WHERE id_producto = ?")) {
                asignarParametros(ps, new Object[] {producto.getNombre().trim(), producto.getCategoria().getId(),
                        producto.getStockMinimo(), producto.getId()});
                ps.executeUpdate();
            }
            pedidoDAO.evaluar(con, producto.getId(), actual.getStockActual(), producto.getStockMinimo());
            return null;
        });
    }

    /**
     * UC-02 (baja): borra las ofertas de proveedores del producto y luego el producto. Si tiene
     * movimientos o pedidos registrados, MySQL rechaza el borrado (clave foránea) y se informa con
     * RegistroEnUsoException; la transacción se revierte y las ofertas no se pierden.
     */
    @Override
    public void eliminar(int idProducto) throws InventarioException {
        ejecutarEnTransaccion("eliminar el producto", con -> {
            buscarPorIdBloqueando(con, idProducto);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM producto_proveedor WHERE id_producto = ?")) {
                ps.setInt(1, idProducto);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM producto WHERE id_producto = ?")) {
                ps.setInt(1, idProducto);
                ps.executeUpdate();
            }
            return null;
        });
    }

    /**
     * Lee el producto dentro de una transacción en curso y bloquea su fila (SELECT ... FOR UPDATE), de
     * modo que dos operarios que registren movimientos del mismo producto al mismo tiempo no pisen
     * el stock del otro (RNF-02).
     */
    public Producto buscarPorIdBloqueando(Connection con, int idProducto)
            throws SQLException, ProductoNoEncontradoException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE p.id_producto = ? FOR UPDATE")) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ProductoNoEncontradoException(idProducto);
                }
                return mapear(rs);
            }
        }
    }

    /** Actualiza el stock_actual de un producto reutilizando la conexión de la transacción en curso. */
    public void actualizarStock(Connection con, int idProducto, int nuevoStock) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE producto SET stock_actual = ? WHERE id_producto = ?")) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }
}
