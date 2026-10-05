package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.excepciones.ProductoNoEncontradoException;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos (JDBC) para la tabla producto, con el join a categoria ya resuelto. */
public class ProductoDAO {

    private static final String SELECT_BASE =
            "SELECT p.id_producto, p.nombre, p.stock_actual, p.stock_minimo, " +
            "       c.id_categoria, c.nombre AS nombre_categoria " +
            "FROM producto p JOIN categoria c ON c.id_categoria = p.id_categoria ";

    /** UC-11: listar el stock de todos los productos, agrupado por categoría. */
    public List<Producto> listarTodos() throws PersistenciaException {
        List<Producto> productos = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY c.nombre, p.nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar los productos.", e);
        }
        return productos;
    }

    public Producto buscarPorId(int idProducto) throws PersistenciaException, ProductoNoEncontradoException {
        String sql = SELECT_BASE + "WHERE p.id_producto = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ProductoNoEncontradoException(idProducto);
                }
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el producto.", e);
        }
    }

    /**
     * Lee el producto dentro de una transacción en curso y bloquea su fila (SELECT ... FOR UPDATE), de
     * modo que dos operarios que registren movimientos del mismo producto al mismo tiempo no pisen
     * el stock del otro (RNF-02).
     */
    public Producto buscarPorIdBloqueando(Connection con, int idProducto)
            throws SQLException, ProductoNoEncontradoException {
        String sql = SELECT_BASE + "WHERE p.id_producto = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
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
        String sql = "UPDATE producto SET stock_actual = ? WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("nombre_categoria"));
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                categoria,
                rs.getInt("stock_actual"),
                rs.getInt("stock_minimo")
        );
    }
}
