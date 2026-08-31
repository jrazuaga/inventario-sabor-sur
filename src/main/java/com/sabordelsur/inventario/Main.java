package com.sabordelsur.inventario;

import com.sabordelsur.inventario.modelo.MovimientoStock;
import com.sabordelsur.inventario.modelo.PedidoReposicion;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import com.sabordelsur.inventario.modelo.TipoMovimiento;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.MovimientoStockDAO;
import com.sabordelsur.inventario.persistencia.PedidoReposicionDAO;
import com.sabordelsur.inventario.persistencia.ProductoDAO;
import com.sabordelsur.inventario.persistencia.ProductoProveedorDAO;
import com.sabordelsur.inventario.persistencia.UsuarioDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Prototipo de consola que ejercita, sobre la base sabor_sur_db, los casos de uso
 * centrales del sistema: UC-01 (iniciar sesión), UC-11 (consultar stock),
 * UC-05 (comparar proveedores), UC-08/09/10 (registrar movimiento y reposición
 * automática) y UC-07 (ver pedidos pendientes).
 *
 * Requiere una instancia de MySQL local con la base creada a partir de
 * sabor_sur_db.sql y las credenciales configuradas en ConexionBD.
 */
public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final ProductoDAO productoDAO = new ProductoDAO();
    private static final ProductoProveedorDAO productoProveedorDAO = new ProductoProveedorDAO();
    private static final MovimientoStockDAO movimientoDAO = new MovimientoStockDAO();
    private static final PedidoReposicionDAO pedidoDAO = new PedidoReposicionDAO();

    public static void main(String[] args) {
        System.out.println("=== Sistema de Gestión de Inventario y Reposición — Distribuidora Sabor Sur S.R.L. ===");

        Usuario usuario = iniciarSesion();
        if (usuario == null) {
            System.out.println("No fue posible iniciar sesión. Fin del programa.");
            return;
        }
        System.out.println("Sesión iniciada como " + usuario);

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1:
                        consultarStock();
                        break;
                    case 2:
                        compararProveedores();
                        break;
                    case 3:
                        registrarMovimiento(usuario);
                        break;
                    case 4:
                        verPedidosPendientes();
                        break;
                    case 0:
                        System.out.println("Fin del programa.");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static Usuario iniciarSesion() {
        System.out.print("Usuario: ");
        String nombreUsuario = SC.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = SC.nextLine();
        try {
            Usuario usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
            if (usuario != null && usuario.iniciarSesion(contrasena)) {
                return usuario;
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Error al validar las credenciales: " + e.getMessage());
            return null;
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n--- Menú ---");
        System.out.println("1) Consultar stock de productos (UC-11)");
        System.out.println("2) Comparar condiciones de proveedores (UC-05)");
        System.out.println("3) Registrar movimiento de stock (UC-08)");
        System.out.println("4) Ver pedidos de reposición pendientes (UC-07)");
        System.out.println("0) Salir");
    }

    private static void consultarStock() throws SQLException {
        List<Producto> productos = productoDAO.listarTodos();
        productos.forEach(p -> System.out.printf(
                "  [%d] %-25s %-15s stock: %d / mínimo: %d%n",
                p.getId(), p.getNombre(), p.getCategoria(), p.getStockActual(), p.getStockMinimo()
        ));
    }

    private static void compararProveedores() throws SQLException {
        int idProducto = leerEntero("ID del producto: ");
        List<ProductoProveedor> ofertas = productoProveedorDAO.listarPorProducto(idProducto);
        if (ofertas.isEmpty()) {
            System.out.println("  No hay proveedores registrados para ese producto.");
            return;
        }
        ofertas.forEach(o -> System.out.printf(
                "  %-25s $ %10.2f  disponible: %s%n",
                o.getProveedor().getNombre(), o.getPrecio(), o.isDisponible() ? "sí" : "no"
        ));
    }

    private static void registrarMovimiento(Usuario usuario) throws SQLException {
        int idProducto = leerEntero("ID del producto: ");
        System.out.print("Tipo (ENTRADA/SALIDA): ");
        TipoMovimiento tipo = TipoMovimiento.valueOf(SC.nextLine().trim().toUpperCase());
        int cantidad = leerEntero("Cantidad: ");

        Producto producto = productoDAO.buscarPorId(idProducto);
        if (producto == null) {
            System.out.println("  Producto inexistente.");
            return;
        }

        MovimientoStock movimiento = new MovimientoStock(producto, usuario, tipo, cantidad);
        movimientoDAO.registrarMovimiento(movimiento);
        System.out.println("  Movimiento registrado. Stock y cola de reposición actualizados.");
    }

    private static void verPedidosPendientes() throws SQLException {
        List<PedidoReposicion> pedidos = pedidoDAO.listarPendientes();
        if (pedidos.isEmpty()) {
            System.out.println("  No hay pedidos de reposición pendientes.");
            return;
        }
        pedidos.forEach(p -> System.out.println("  " + p));
    }

    private static int leerEntero(String etiqueta) {
        System.out.print(etiqueta);
        while (!SC.hasNextInt()) {
            System.out.print("Ingrese un número válido. " + etiqueta);
            SC.next();
        }
        int valor = SC.nextInt();
        SC.nextLine();
        return valor;
    }
}
