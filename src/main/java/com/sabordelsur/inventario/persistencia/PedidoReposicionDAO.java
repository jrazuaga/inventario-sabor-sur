package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.modelo.EstadoPedido;
import com.sabordelsur.inventario.modelo.PedidoReposicion;
import com.sabordelsur.inventario.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos (JDBC) para la tabla pedido_reposicion. */
public class PedidoReposicionDAO {

    /** UC-07: listar los pedidos de reposición pendientes, con el producto asociado. */
    public List<PedidoReposicion> listarPendientes() throws SQLException {
        String sql =
                "SELECT pr.id_pedido, pr.fecha_generacion, pr.estado, " +
                "       p.id_producto, p.nombre, p.stock_actual, p.stock_minimo " +
                "FROM pedido_reposicion pr JOIN producto p ON p.id_producto = pr.id_producto " +
                "WHERE pr.estado = 'PENDIENTE' " +
                "ORDER BY pr.fecha_generacion";

        List<PedidoReposicion> pedidos = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id_producto"), rs.getString("nombre"), null,
                        rs.getInt("stock_actual"), rs.getInt("stock_minimo")
                );
                PedidoReposicion pedido = new PedidoReposicion(producto);
                pedido.setId(rs.getInt("id_pedido"));
                pedido.setFechaGeneracion(rs.getTimestamp("fecha_generacion").toLocalDateTime());
                pedido.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                pedidos.add(pedido);
            }
        }
        return pedidos;
    }

    /** Devuelve true si ya existe un pedido PENDIENTE para el producto indicado (evita duplicados, según UC-09). */
    public boolean existePedidoPendiente(Connection con, int idProducto) throws SQLException {
        String sql = "SELECT 1 FROM pedido_reposicion WHERE id_producto = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** UC-09: genera un nuevo pedido de reposición para el producto, dentro de la transacción en curso. */
    public void generarPedido(Connection con, int idProducto) throws SQLException {
        String sql = "INSERT INTO pedido_reposicion (id_producto, fecha_generacion, estado) VALUES (?, ?, 'PENDIENTE')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setObject(2, LocalDateTime.now());
            ps.executeUpdate();
        }
    }

    /** UC-10: retira de la cola (marca como RESUELTO) el pedido pendiente del producto indicado, si existe. */
    public void resolverPedidoSiExiste(Connection con, int idProducto) throws SQLException {
        String sql = "UPDATE pedido_reposicion SET estado = 'RESUELTO' " +
                     "WHERE id_producto = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }
}
