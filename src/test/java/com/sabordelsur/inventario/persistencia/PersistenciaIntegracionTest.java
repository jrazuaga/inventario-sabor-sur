package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.DatoDuplicadoException;
import com.sabordelsur.inventario.excepciones.RegistroEnUsoException;
import com.sabordelsur.inventario.excepciones.StockInsuficienteException;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.MovimientoStock;
import com.sabordelsur.inventario.modelo.PedidoReposicion;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ResultadoMovimiento;
import com.sabordelsur.inventario.modelo.TipoMovimiento;
import com.sabordelsur.inventario.modelo.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Pruebas de integración contra la base MySQL real (sabor_sur_db). Solo se ejecutan si la variable de
 * entorno SABOR_SUR_IT vale 1 y la base responde; en cualquier otro caso se omiten, para que el build
 * no dependa de tener MySQL instalado. Cada prueba crea un producto propio y lo borra al terminar.
 */
class PersistenciaIntegracionTest {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoReposicionDAO pedidoDAO = new PedidoReposicionDAO();
    private final MovimientoStockDAO movimientoDAO = new MovimientoStockDAO();
    private Producto producto;

    @BeforeEach
    void preparar() throws Exception {
        assumeTrue("1".equals(System.getenv("SABOR_SUR_IT")) && ConexionBD.estaDisponible(),
                "Base de datos no disponible: se omiten las pruebas de integración.");
        producto = new Producto(0, "Producto de prueba IT " + System.nanoTime(), new Categoria(1, "Aceites"), 3, 5);
    }

    @AfterEach
    void limpiar() throws Exception {
        if (producto != null && producto.getId() != 0) {
            try (java.sql.Connection con = ConexionBD.obtenerConexion();
                 java.sql.Statement st = con.createStatement()) {
                st.executeUpdate("DELETE FROM pedido_reposicion WHERE id_producto = " + producto.getId());
                st.executeUpdate("DELETE FROM movimiento_stock WHERE id_producto = " + producto.getId());
                st.executeUpdate("DELETE FROM producto WHERE id_producto = " + producto.getId());
            }
        }
    }

    private boolean tienePedidoPendiente(int idProducto) throws Exception {
        for (PedidoReposicion p : pedidoDAO.listarPendientes()) {
            if (p.getProducto().getId() == idProducto) {
                return true;
            }
        }
        return false;
    }

    @Test
    void altaConStockBajoElMinimoGeneraPedido() throws Exception {
        productoDAO.insertar(producto);
        assertTrue(producto.getId() > 0);
        assertTrue(tienePedidoPendiente(producto.getId()));
    }

    @Test
    void bajarElMinimoResuelveElPedidoPendiente() throws Exception {
        productoDAO.insertar(producto);
        producto.setStockMinimo(2);
        productoDAO.actualizar(producto);
        assertFalse(tienePedidoPendiente(producto.getId()));
    }

    @Test
    void nombreRepetidoLanzaDatoDuplicado() throws Exception {
        productoDAO.insertar(producto);
        Producto copia = new Producto(0, producto.getNombre(), new Categoria(1, "Aceites"), 1, 1);
        assertThrows(DatoDuplicadoException.class, () -> productoDAO.insertar(copia));
    }

    @Test
    void salidaMayorAlStockSeRevierteSinCambios() throws Exception {
        productoDAO.insertar(producto);
        Usuario operario = new UsuarioDAO().autenticar("operario1", "operario123");
        MovimientoStock salida = MovimientoStock.crear(TipoMovimiento.SALIDA, producto, operario, 50);
        assertThrows(StockInsuficienteException.class, () -> movimientoDAO.registrarMovimiento(salida));
        assertEquals(3, productoDAO.buscarPorId(producto.getId()).getStockActual());
    }

    @Test
    void entradaQueSuperaElMinimoResuelveElPedido() throws Exception {
        productoDAO.insertar(producto);
        Usuario operario = new UsuarioDAO().autenticar("operario1", "operario123");
        ResultadoMovimiento r = movimientoDAO.registrarMovimiento(
                MovimientoStock.crear(TipoMovimiento.ENTRADA, producto, operario, 10));
        assertEquals(13, r.getStockResultante());
        assertTrue(r.isPedidoResuelto());
    }

    @Test
    void productoConMovimientosNoSePuedeEliminar() throws Exception {
        productoDAO.insertar(producto);
        Usuario operario = new UsuarioDAO().autenticar("operario1", "operario123");
        movimientoDAO.registrarMovimiento(MovimientoStock.crear(TipoMovimiento.ENTRADA, producto, operario, 1));
        assertThrows(RegistroEnUsoException.class, () -> productoDAO.eliminar(producto.getId()));
        assertEquals(producto.getNombre(), productoDAO.buscarPorId(producto.getId()).getNombre());
    }
}
