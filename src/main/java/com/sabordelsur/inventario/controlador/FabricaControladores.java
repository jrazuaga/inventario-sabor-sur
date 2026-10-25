package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.persistencia.CategoriaDAO;
import com.sabordelsur.inventario.persistencia.MovimientoStockDAO;
import com.sabordelsur.inventario.persistencia.PedidoReposicionDAO;
import com.sabordelsur.inventario.persistencia.ProductoDAO;
import com.sabordelsur.inventario.persistencia.ProductoProveedorDAO;
import com.sabordelsur.inventario.persistencia.ProveedorDAO;
import com.sabordelsur.inventario.persistencia.UsuarioDAO;

/**
 * Crea los controladores con sus dependencias (DAO y bitácora) en un único lugar. Así las vistas
 * (interfaz gráfica o consola) no instancian clases de persistencia: solo piden el controlador que
 * necesitan. Cada controlador de la sesión de trabajo se crea una sola vez y se reutiliza.
 */
public class FabricaControladores {

    private final Bitacora bitacora = new Bitacora();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final MovimientoStockDAO movimientoDAO = new MovimientoStockDAO();

    private final ControladorSesion sesion = new ControladorSesion(new UsuarioDAO(), bitacora);
    private final ControladorStock stock = new ControladorStock(productoDAO);
    private final ControladorProductos productos =
            new ControladorProductos(productoDAO, new CategoriaDAO(), bitacora);
    private final ControladorProveedores proveedores =
            new ControladorProveedores(new ProveedorDAO(), new ProductoProveedorDAO(), bitacora);
    private final ControladorMovimientos movimientos =
            new ControladorMovimientos(productoDAO, movimientoDAO, bitacora);
    private final ControladorPedidos pedidos = new ControladorPedidos(new PedidoReposicionDAO());
    private final ControladorUsuarios usuarios = new ControladorUsuarios(new UsuarioDAO(), bitacora);
    private final ControladorReportes reportes =
            new ControladorReportes(productoDAO, movimientoDAO, bitacora, ControladorReportes.CARPETA_POR_DEFECTO);

    public ControladorSesion sesion() {
        return sesion;
    }

    public ControladorStock stock() {
        return stock;
    }

    public ControladorProductos productos() {
        return productos;
    }

    public ControladorProveedores proveedores() {
        return proveedores;
    }

    public ControladorMovimientos movimientos() {
        return movimientos;
    }

    public ControladorPedidos pedidos() {
        return pedidos;
    }

    public ControladorUsuarios usuarios() {
        return usuarios;
    }

    public ControladorReportes reportes() {
        return reportes;
    }
}
