package com.sabordelsur.inventario;

import com.sabordelsur.inventario.algoritmos.Busqueda;
import com.sabordelsur.inventario.algoritmos.Ordenamiento;
import com.sabordelsur.inventario.estructuras.ColaEnlazada;
import com.sabordelsur.inventario.estructuras.PilaEnlazada;
import com.sabordelsur.inventario.excepciones.CredencialesInvalidasException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.MovimientoStock;
import com.sabordelsur.inventario.modelo.OpcionMenu;
import com.sabordelsur.inventario.modelo.PedidoReposicion;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import com.sabordelsur.inventario.modelo.ResultadoMovimiento;
import com.sabordelsur.inventario.modelo.TipoMovimiento;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.MovimientoStockDAO;
import com.sabordelsur.inventario.persistencia.PedidoReposicionDAO;
import com.sabordelsur.inventario.persistencia.ProductoDAO;
import com.sabordelsur.inventario.persistencia.ProductoProveedorDAO;
import com.sabordelsur.inventario.persistencia.UsuarioDAO;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Prototipo de consola que ejercita, sobre la base sabor_sur_db, los casos de uso centrales del sistema:
 * UC-01 (iniciar sesión), UC-11 (consultar stock), UC-05 (comparar proveedores), UC-08/09/10 (registrar
 * movimiento y reposición automática) y UC-07 (ver pedidos pendientes). El menú que se muestra depende
 * del tipo de usuario que inició sesión (Administrador u Operador).
 *
 * Requiere una instancia de MySQL local con la base creada a partir de sabor_sur_db.sql.
 */
public class Main {

    private static final int MAX_INTENTOS_LOGIN = 3;

    private final Consola consola = new Consola(new Scanner(System.in));
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ProductoProveedorDAO productoProveedorDAO = new ProductoProveedorDAO();
    private final MovimientoStockDAO movimientoDAO = new MovimientoStockDAO();
    private final PedidoReposicionDAO pedidoDAO = new PedidoReposicionDAO();

    /** Pila con los movimientos registrados en esta sesión: el último registrado queda arriba. */
    private final PilaEnlazada<MovimientoStock> historialSesion = new PilaEnlazada<>();

    public static void main(String[] args) {
        System.out.println("=== Sistema de Gestión de Inventario y Reposición — Distribuidora Sabor Sur S.R.L. ===");
        try {
            new Main().ejecutar();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada. Fin del programa.");
        }
    }

    private void ejecutar() {
        Usuario usuario = iniciarSesion();
        if (usuario == null) {
            System.out.println("No fue posible iniciar sesión. Fin del programa.");
            return;
        }
        System.out.println("Sesión iniciada como " + usuario);

        int seleccion;
        do {
            List<OpcionMenu> opciones = usuario.getOpcionesPermitidas();
            mostrarMenu(opciones);
            seleccion = consola.leerEnteroEnRango("Opción: ", 0, opciones.size());
            if (seleccion == 0) {
                System.out.println("Fin del programa.");
            } else {
                ejecutarOpcion(opciones.get(seleccion - 1), usuario);
            }
        } while (seleccion != 0);
    }

    /** UC-01: pide las credenciales hasta MAX_INTENTOS_LOGIN veces. Devuelve null si no se logra ingresar. */
    private Usuario iniciarSesion() {
        for (int intento = 1; intento <= MAX_INTENTOS_LOGIN; intento++) {
            String nombre = consola.leerTexto("Usuario: ");
            String contrasena = consola.leerTexto("Contraseña: ");
            try {
                return usuarioDAO.autenticar(nombre, contrasena);
            } catch (CredencialesInvalidasException e) {
                System.out.printf("%s Intento %d de %d.%n", e.getMessage(), intento, MAX_INTENTOS_LOGIN);
            } catch (InventarioException e) {
                System.out.println("Error: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    private void mostrarMenu(List<OpcionMenu> opciones) {
        System.out.println("\n--- Menú ---");
        for (int i = 0; i < opciones.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, opciones.get(i).getDescripcion());
        }
        System.out.println("0) Salir");
    }

    private void ejecutarOpcion(OpcionMenu opcion, Usuario usuario) {
        try {
            switch (opcion) {
                case CONSULTAR_STOCK:
                    consultarStock();
                    break;
                case BUSCAR_PRODUCTO:
                    buscarProducto();
                    break;
                case COMPARAR_PROVEEDORES:
                    compararProveedores();
                    break;
                case VER_COLA_PEDIDOS:
                    verColaPedidos();
                    break;
                case VER_PRODUCTOS_CRITICOS:
                    verProductosCriticos();
                    break;
                case REGISTRAR_MOVIMIENTO:
                    registrarMovimiento(usuario);
                    break;
                case VER_HISTORIAL_SESION:
                    verHistorialSesion();
                    break;
                default:
                    System.out.println("Opción no disponible.");
            }
        } catch (InventarioException e) {
            System.out.println("  No se pudo completar la operación: " + e.getMessage());
        }
    }

    private void consultarStock() throws InventarioException {
        for (Producto p : productoDAO.listarTodos()) {
            System.out.printf("  [%d] %-25s %-14s stock: %3d / mínimo: %3d%n",
                    p.getId(), p.getNombre(), p.getCategoria(), p.getStockActual(), p.getStockMinimo());
        }
    }

    /**
     * Busca un producto por nombre. Primero ordena el catálogo por nombre (ordenamiento por mezcla) y
     * prueba una coincidencia exacta con búsqueda binaria; si no la hay, lista las coincidencias
     * parciales con búsqueda lineal.
     */
    private void buscarProducto() throws InventarioException {
        String texto = consola.leerTexto("Nombre (o parte del nombre): ");
        if (texto.isEmpty()) {
            System.out.println("  Debe ingresar un texto para buscar.");
            return;
        }
        List<Producto> ordenados = Ordenamiento.ordenarPorMezcla(productoDAO.listarTodos(), Comparator.naturalOrder());
        int posicion = Busqueda.binariaPorNombre(ordenados, texto);
        if (posicion >= 0) {
            System.out.println("  Coincidencia exacta: " + ordenados.get(posicion));
            return;
        }
        List<Producto> parciales = Busqueda.linealPorTexto(ordenados, texto);
        if (parciales.isEmpty()) {
            System.out.println("  No se encontraron productos que coincidan con \"" + texto + "\".");
            return;
        }
        System.out.println("  Productos que contienen \"" + texto + "\":");
        for (Producto p : parciales) {
            System.out.println("   - [" + p.getId() + "] " + p);
        }
    }

    /** UC-05: muestra las ofertas de los proveedores de un producto ordenadas por el criterio elegido. */
    private void compararProveedores() throws InventarioException {
        int idProducto = consola.leerEntero("ID del producto: ");
        Producto producto = productoDAO.buscarPorId(idProducto);
        List<ProductoProveedor> ofertas = productoProveedorDAO.listarPorProducto(producto);
        if (ofertas.isEmpty()) {
            System.out.println("  No hay proveedores registrados para " + producto.getNombre() + ".");
            return;
        }
        int criterio = consola.leerEnteroEnRango("Ordenar por: 1) precio  2) plazo de entrega: ", 1, 2);
        Ordenamiento.ordenarPorInsercion(ofertas,
                criterio == 1 ? ProductoProveedor.POR_PRECIO : ProductoProveedor.POR_PLAZO);

        System.out.println("  Ofertas para " + producto.getNombre() + ":");
        for (ProductoProveedor o : ofertas) {
            System.out.printf("   %-30s $ %10.2f   entrega: %2d días   %s%n",
                    o.getProveedor().getNombre(), o.getPrecio(), o.getPlazoEntregaDias(),
                    o.isDisponible() ? "disponible" : "no disponible");
        }
    }

    /** UC-07: carga los pedidos pendientes en una cola FIFO y los muestra en el orden en que se atenderán. */
    private void verColaPedidos() throws InventarioException {
        ColaEnlazada<PedidoReposicion> cola = new ColaEnlazada<>();
        for (PedidoReposicion pedido : pedidoDAO.listarPendientes()) {
            cola.encolar(pedido);
        }
        if (cola.estaVacia()) {
            System.out.println("  No hay pedidos de reposición pendientes.");
            return;
        }
        System.out.println("  Pedidos pendientes (" + cola.tamanio() + "), en orden de atención:");
        int posicion = 1;
        for (PedidoReposicion pedido : cola) {
            System.out.printf("   %d. %s (generado el %s)%n", posicion++, pedido,
                    pedido.getFechaGeneracion().toLocalDate());
        }
        System.out.println("  Próximo a gestionar: " + cola.verFrente().getProducto().getNombre());
    }

    /** Lista todos los productos del más crítico al menos crítico (ordenamiento por mezcla). */
    private void verProductosCriticos() throws InventarioException {
        List<Producto> ordenados = Ordenamiento.ordenarPorMezcla(productoDAO.listarTodos(),
                Comparator.comparingDouble(Producto::getNivelCriticidad));
        for (Producto p : ordenados) {
            System.out.printf("  %-25s stock %3d / mínimo %3d   %s%n", p.getNombre(), p.getStockActual(),
                    p.getStockMinimo(), p.estaPorDebajoDelMinimo() ? "BAJO EL MÍNIMO" : "ok");
        }
    }

    /** UC-08: registra una entrada o salida. Si corresponde, dispara UC-09 / UC-10 dentro de la misma transacción. */
    private void registrarMovimiento(Usuario usuario) throws InventarioException {
        int idProducto = consola.leerEntero("ID del producto: ");
        Producto producto = productoDAO.buscarPorId(idProducto);
        TipoMovimiento tipo = leerTipoMovimiento();
        int cantidad = consola.leerEntero("Cantidad: ");

        MovimientoStock movimiento = MovimientoStock.crear(tipo, producto, usuario, cantidad);
        ResultadoMovimiento resultado = movimientoDAO.registrarMovimiento(movimiento);
        historialSesion.apilar(movimiento);

        System.out.printf("  Movimiento registrado. Stock de %s: %d.%n", producto.getNombre(), resultado.getStockResultante());
        if (resultado.isPedidoGenerado()) {
            System.out.println("  Stock por debajo del mínimo: se generó un pedido de reposición.");
        }
        if (resultado.isPedidoResuelto()) {
            System.out.println("  Stock recompuesto: se resolvió el pedido de reposición pendiente.");
        }
    }

    private TipoMovimiento leerTipoMovimiento() {
        while (true) {
            String texto = consola.leerTexto("Tipo (ENTRADA/SALIDA): ").toUpperCase();
            try {
                return TipoMovimiento.valueOf(texto);
            } catch (IllegalArgumentException e) {
                System.out.println("  Tipo inválido: escriba ENTRADA o SALIDA.");
            }
        }
    }

    private void verHistorialSesion() {
        if (historialSesion.estaVacia()) {
            System.out.println("  Todavía no se registraron movimientos en esta sesión.");
            return;
        }
        System.out.println("  Movimientos de esta sesión (del más reciente al más antiguo):");
        for (MovimientoStock m : historialSesion) {
            System.out.printf("   %-8s %3d x %s%n", m.getTipo(), m.getCantidad(), m.getProducto().getNombre());
        }
    }
}
