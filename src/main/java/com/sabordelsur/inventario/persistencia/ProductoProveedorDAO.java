package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.PersistenciaException;
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

    /**
     * UC-05: devuelve las ofertas de los proveedores asociados a un producto. El orden lo decide quien
     * llama, aplicando los algoritmos del paquete algoritmos según el criterio de comparación elegido.
     */
    public List<ProductoProveedor> listarPorProducto(Producto producto) throws PersistenciaException {
        String sql =
                "SELECT pv.id_proveedor, pv.nombre AS nombre_proveedor, pv.contacto, " +
                "       pp.precio, pp.plazo_entrega_dias, pp.disponible " +
                "FROM producto_proveedor pp " +
                "JOIN proveedor pv ON pv.id_proveedor = pp.id_proveedor " +
                "WHERE pp.id_producto = ?";

        List<ProductoProveedor> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor proveedor = new Proveedor(
                            rs.getInt("id_proveedor"),
                            rs.getString("nombre_proveedor"),
                            rs.getString("contacto")
                    );
                    resultado.add(new ProductoProveedor(
                            producto, proveedor, rs.getDouble("precio"),
                            rs.getInt("plazo_entrega_dias"), rs.getBoolean("disponible")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar los proveedores del producto.", e);
        }
        return resultado;
    }
}
