package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.modelo.MovimientoStock;
import com.sabordelsur.inventario.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Acceso a datos (JDBC) para movimiento_stock. registrarMovimiento() implementa, en una única
 * transacción, la secuencia completa descripta en las fichas UC-08 / UC-09 / UC-10:
 *   1) inserta el movimiento,
 *   2) actualiza el stock_actual del producto,
 *   3) genera un pedido de reposición si el stock resultante quedó por debajo del mínimo (UC-09), o
 *   4) resuelve el pedido pendiente si el stock resultante lo supera (UC-10).
 * Las cuatro operaciones se ejecutan bajo la misma conexión con autoCommit desactivado, de modo que
 * ante cualquier error se revierta todo el movimiento (stock consistente con la cola de pedidos).
 */
public class MovimientoStockDAO {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoReposicionDAO pedidoDAO = new PedidoReposicionDAO();

    public void registrarMovimiento(MovimientoStock movimiento) throws SQLException {
        String sqlInsert = "INSERT INTO movimiento_stock (id_producto, id_usuario, fecha, tipo, cantidad) " +
                            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                // 1) registrar el movimiento
                try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                    ps.setInt(1, movimiento.getProducto().getId());
                    ps.setInt(2, movimiento.getUsuario().getId());
                    ps.setObject(3, movimiento.getFecha() != null ? movimiento.getFecha() : LocalDateTime.now());
                    ps.setString(4, movimiento.getTipo().name());
                    ps.setInt(5, movimiento.getCantidad());
                    ps.executeUpdate();
                }

                // 2) recalcular y persistir el nuevo stock
                Producto producto = productoDAO.buscarPorId(movimiento.getProducto().getId());
                int nuevoStock = producto.getStockActual() + movimiento.getVariacionStock();
                productoDAO.actualizarStock(con, producto.getId(), nuevoStock);

                // 3) / 4) disparar la lógica de reposición automática (UC-09 / UC-10)
                if (nuevoStock < producto.getStockMinimo()) {
                    if (!pedidoDAO.existePedidoPendiente(con, producto.getId())) {
                        pedidoDAO.generarPedido(con, producto.getId());
                    }
                } else {
                    pedidoDAO.resolverPedidoSiExiste(con, producto.getId());
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
