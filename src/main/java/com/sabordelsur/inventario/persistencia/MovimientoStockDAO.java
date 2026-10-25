package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.AccionReposicion;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.MovimientoStock;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ResultadoMovimiento;
import com.sabordelsur.inventario.modelo.Rol;
import com.sabordelsur.inventario.modelo.TipoMovimiento;
import com.sabordelsur.inventario.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Acceso a datos (JDBC) para movimiento_stock. registrarMovimiento() implementa, en una única
 * transacción, la secuencia completa descripta en las fichas UC-08 / UC-09 / UC-10:
 *   1) bloquea la fila del producto y valida el movimiento contra el stock real,
 *   2) inserta el movimiento,
 *   3) actualiza el stock_actual del producto,
 *   4) aplica la regla de reposición: genera el pedido (UC-09) o resuelve el pendiente (UC-10).
 * Ante cualquier error se revierte el movimiento completo y el stock queda consistente con la cola de pedidos.
 */
public class MovimientoStockDAO extends DAOBase<MovimientoStock> {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoReposicionDAO pedidoDAO = new PedidoReposicionDAO();

    @Override
    protected String getNombreEntidad() {
        return "movimiento de stock";
    }

    @Override
    protected MovimientoStock mapear(ResultSet rs) throws SQLException {
        Producto producto = new Producto(rs.getInt("id_producto"), rs.getString("nombre_producto"),
                new Categoria(rs.getInt("id_categoria"), rs.getString("nombre_categoria")),
                rs.getInt("stock_actual"), rs.getInt("stock_minimo"));
        // El hash de la contraseña no se lee: para mostrar el historial alcanza con el nombre y el rol.
        Usuario usuario = Usuario.crear(Rol.valueOf(rs.getString("rol")), rs.getInt("id_usuario"),
                rs.getString("nombre_usuario"), null);
        MovimientoStock movimiento = MovimientoStock.crear(TipoMovimiento.valueOf(rs.getString("tipo")),
                producto, usuario, rs.getInt("cantidad"));
        movimiento.setId(rs.getInt("id_movimiento"));
        movimiento.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        return movimiento;
    }

    public ResultadoMovimiento registrarMovimiento(MovimientoStock movimiento) throws InventarioException {
        ResultadoMovimiento resultado = ejecutarEnTransaccion("registrar el movimiento", con -> {
            // 1) leer el stock real con la fila bloqueada y validar el movimiento
            Producto producto = productoDAO.buscarPorIdBloqueando(con, movimiento.getProducto().getId());
            movimiento.getProducto().setStockActual(producto.getStockActual());
            movimiento.validar();

            // 2) registrar el movimiento
            insertar(con, movimiento);

            // 3) recalcular y persistir el nuevo stock
            int nuevoStock = producto.getStockActual() + movimiento.getVariacionStock();
            productoDAO.actualizarStock(con, producto.getId(), nuevoStock);

            // 4) disparar la lógica de reposición automática (UC-09 / UC-10)
            AccionReposicion accion =
                    pedidoDAO.evaluar(con, producto.getId(), nuevoStock, producto.getStockMinimo());
            return new ResultadoMovimiento(nuevoStock,
                    accion == AccionReposicion.PEDIDO_GENERADO, accion == AccionReposicion.PEDIDO_RESUELTO);
        });
        movimiento.getProducto().setStockActual(resultado.getStockResultante());
        return resultado;
    }

    /** Últimos movimientos registrados en la base (para la interfaz y el reporte CSV), del más reciente al más antiguo. */
    public List<MovimientoStock> listarRecientes(int limite) throws PersistenciaException {
        return consultarLista(
                "SELECT m.id_movimiento, m.fecha, t.nombre AS tipo, m.cantidad, " +
                "       p.id_producto, p.nombre AS nombre_producto, p.stock_actual, p.stock_minimo, " +
                "       c.id_categoria, c.nombre AS nombre_categoria, " +
                "       u.id_usuario, u.nombre_usuario, r.nombre AS rol " +
                "FROM movimiento_stock m " +
                "JOIN tipo_movimiento t ON t.id_tipo      = m.id_tipo " +
                "JOIN producto p        ON p.id_producto  = m.id_producto " +
                "JOIN categoria c       ON c.id_categoria = p.id_categoria " +
                "JOIN usuario u         ON u.id_usuario   = m.id_usuario " +
                "JOIN rol r             ON r.id_rol       = u.id_rol " +
                "ORDER BY m.fecha DESC, m.id_movimiento DESC LIMIT ?", limite);
    }

    private void insertar(Connection con, MovimientoStock movimiento) throws SQLException {
        String sql = "INSERT INTO movimiento_stock (id_producto, id_usuario, fecha, id_tipo, cantidad) " +
                     "VALUES (?, ?, ?, (SELECT id_tipo FROM tipo_movimiento WHERE nombre = ?), ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            asignarParametros(ps, new Object[] {movimiento.getProducto().getId(), movimiento.getUsuario().getId(),
                    Timestamp.valueOf(movimiento.getFecha()), movimiento.getTipo().name(), movimiento.getCantidad()});
            ps.executeUpdate();
        }
    }
}
