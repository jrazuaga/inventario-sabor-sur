package com.sabordelsur.inventario.persistencia;

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
    public List<Producto> listarTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY c.nombre, p.nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        }
        return productos;
    }

    public Producto buscarPorId(int idProducto) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.id_producto = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /**
     * Actualiza el stock_actual de un producto. Se usa desde MovimientoStockDAO dentro de una
     * misma transacción, reutilizando la conexión que ya tiene abierta la transacción en curso.
     */
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
