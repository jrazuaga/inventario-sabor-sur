package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.MovimientoStock;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ResultadoMovimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Acceso a datos (JDBC) para movimiento_stock. registrarMovimiento() implementa, en una única
 * transacción, la secuencia completa descripta en las fichas UC-08 / UC-09 / UC-10:
 *   1) bloquea la fila del producto y valida el movimiento contra el stock real,
 *   2) inserta el movimiento,
 *   3) actualiza el stock_actual del producto,
 *   4) genera un pedido de reposición si el stock resultante quedó por debajo del mínimo (UC-09), o
 *   5) resuelve el pedido pendiente si el stock resultante lo supera (UC-10).
 * Todo ocurre bajo la misma conexión con autoCommit desactivado: ante cualquier error se revierte el
 * movimiento completo y el stock queda consistente con la cola de pedidos.
 */
public class MovimientoStockDAO {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoReposicionDAO pedidoDAO = new PedidoReposicionDAO();

    public ResultadoMovimiento registrarMovimiento(MovimientoStock movimiento) throws InventarioException {
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                // 1) leer el stock real con la fila bloqueada y validar el movimiento
                Producto producto = productoDAO.buscarPorIdBloqueando(con, movimiento.getProducto().getId());
                movimiento.getProducto().setStockActual(producto.getStockActual());
                movimiento.validar();

                // 2) registrar el movimiento
                insertar(con, movimiento);

                // 3) recalcular y persistir el nuevo stock
                int nuevoStock = producto.getStockActual() + movimiento.getVariacionStock();
                productoDAO.actualizarStock(con, producto.getId(), nuevoStock);

                // 4) / 5) disparar la lógica de reposición automática (UC-09 / UC-10)
                boolean pedidoGenerado = false;
                boolean pedidoResuelto = false;
                if (nuevoStock < producto.getStockMinimo()) {
                    if (!pedidoDAO.existePedidoPendiente(con, producto.getId())) {
                        pedidoDAO.generarPedido(con, producto.getId());
                        pedidoGenerado = true;
                    }
                } else {
                    pedidoResuelto = pedidoDAO.resolverPedidoSiExiste(con, producto.getId());
                }

                con.commit();
                movimiento.getProducto().setStockActual(nuevoStock);
                return new ResultadoMovimiento(nuevoStock, pedidoGenerado, pedidoResuelto);
            } catch (SQLException e) {
                revertir(con);
                throw new PersistenciaException("No se pudo registrar el movimiento; no se realizaron cambios.", e);
            } catch (InventarioException e) {
                revertir(con);
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de conexión al registrar el movimiento.", e);
        }
    }

    private void insertar(Connection con, MovimientoStock movimiento) throws SQLException {
        String sql = "INSERT INTO movimiento_stock (id_producto, id_usuario, fecha, id_tipo, cantidad) " +
                     "VALUES (?, ?, ?, (SELECT id_tipo FROM tipo_movimiento WHERE nombre = ?), ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, movimiento.getProducto().getId());
            ps.setInt(2, movimiento.getUsuario().getId());
            ps.setTimestamp(3, Timestamp.valueOf(movimiento.getFecha()));
            ps.setString(4, movimiento.getTipo().name());
            ps.setInt(5, movimiento.getCantidad());
            ps.executeUpdate();
        }
    }

    private void revertir(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ignorada) {
            // Si el rollback falla, la conexión se cierra igual y el motor descarta la transacción abierta.
        }
    }
}
