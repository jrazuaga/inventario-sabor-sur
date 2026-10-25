package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.estructuras.PilaEnlazada;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.MovimientoStock;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ResultadoMovimiento;
import com.sabordelsur.inventario.modelo.TipoMovimiento;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.CrudDAO;
import com.sabordelsur.inventario.persistencia.MovimientoStockDAO;

import java.util.ArrayList;
import java.util.List;

/** UC-08 (registrar movimiento), con UC-09 y UC-10 resueltos por el DAO dentro de la misma transacción. */
public class ControladorMovimientos {

    private final CrudDAO<Producto> productoDAO;
    private final MovimientoStockDAO movimientoDAO;
    private final Bitacora bitacora;

    /** Pila con los movimientos registrados en esta sesión: el último registrado queda arriba. */
    private final PilaEnlazada<MovimientoStock> historialSesion = new PilaEnlazada<>();

    public ControladorMovimientos(CrudDAO<Producto> productoDAO, MovimientoStockDAO movimientoDAO, Bitacora bitacora) {
        this.productoDAO = productoDAO;
        this.movimientoDAO = movimientoDAO;
        this.bitacora = bitacora;
    }

    public ResultadoMovimiento registrar(Usuario usuario, int idProducto, TipoMovimiento tipo, int cantidad)
            throws InventarioException {
        Producto producto = productoDAO.buscarPorId(idProducto);
        MovimientoStock movimiento = MovimientoStock.crear(tipo, producto, usuario, cantidad);
        ResultadoMovimiento resultado = movimientoDAO.registrarMovimiento(movimiento);
        historialSesion.apilar(movimiento);

        String detalle = String.format("%s de %d x %s (stock resultante: %d)", tipo, cantidad,
                producto.getNombre(), resultado.getStockResultante());
        if (resultado.isPedidoGenerado()) {
            detalle += "; se generó un pedido de reposición";
        } else if (resultado.isPedidoResuelto()) {
            detalle += "; se resolvió el pedido de reposición";
        }
        bitacora.registrar(usuario.getNombreUsuario(), detalle);
        return resultado;
    }

    /** Movimientos de la sesión, del más reciente al más antiguo (recorrido de la pila). */
    public List<MovimientoStock> getHistorialSesion() {
        List<MovimientoStock> lista = new ArrayList<>();
        for (MovimientoStock m : historialSesion) {
            lista.add(m);
        }
        return lista;
    }

    public List<MovimientoStock> recientes(int limite) throws PersistenciaException {
        return movimientoDAO.listarRecientes(limite);
    }
}
