package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import com.sabordelsur.inventario.modelo.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos (JDBC) para la tabla producto_proveedor, la clase asociativa del modelo de diseño. */
public class ProductoProveedorDAO {

    /** UC-05: comparar las condiciones de los distintos proveedores para un producto, ordenadas por precio. */
    public List<ProductoProveedor> listarPorProducto(int idProducto) throws SQLException {
        String sql =
                "SELECT pv.id_proveedor, pv.nombre AS nombre_proveedor, pv.contacto, " +
                "       pp.precio, pp.disponible " +
                "FROM producto_proveedor pp " +
                "JOIN proveedor pv ON pv.id_proveedor = pp.id_proveedor " +
                "WHERE pp.id_producto = ? " +
                "ORDER BY pp.precio ASC";

        List<ProductoProveedor> resultado = new ArrayList<>();
        ProductoDAO productoDAO = new ProductoDAO();
        Producto producto = productoDAO.buscarPorId(idProducto);

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor proveedor = new Proveedor(
                            rs.getInt("id_proveedor"),
                            rs.getString("nombre_proveedor"),
                            rs.getString("contacto")
                    );
                    resultado.add(new ProductoProveedor(
                            producto, proveedor, rs.getDouble("precio"), rs.getBoolean("disponible")
                    ));
                }
            }
        }
        return resultado;
    }
}
