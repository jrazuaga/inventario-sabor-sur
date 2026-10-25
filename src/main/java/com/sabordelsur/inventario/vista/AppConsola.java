package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorSesion;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.estructuras.ColaEnlazada;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Vista de consola (la del TP3), conservada como alternativa a la interfaz gráfica: se ejecuta con el
 * argumento --consola o cuando el equipo no tiene entorno gráfico. Usa los mismos controladores que la
 * interfaz Swing, lo que muestra la separación entre vista y lógica que propone el patrón MVC.
 */
public class AppConsola {

    private final Consola consola = new Consola(new Scanner(System.in));
    private final FabricaControladores controladores;

    public AppConsola(FabricaControladores controladores) {
        this.controladores = controladores;
    }

    public void ejecutar() {
        System.out.println("=== Sistema de Gestión de Inventario y Reposición - Distribuidora Sabor Sur S.R.L. ===");
        try {
            Usuario usuario = iniciarSesion();
            if (usuario == null) {
                System.out.println("No fue posible iniciar sesión. Fin del programa.");
                return;
            }
            System.out.println("Sesión iniciada como " + usuario);
            menuPrincipal(usuario);
            controladores.sesion().cerrarSesion(usuario);
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada. Fin del programa.");
        }
    }

    private void menuPrincipal(Usuario usuario) {
        List<OpcionMenu> opciones = new ArrayList<>();
        for (OpcionMenu opcion : usuario.getOpcionesPermitidas()) {
            if (opcion.isDisponibleEnConsola()) {
                opciones.add(opcion);
            }
        }
        int seleccion;
        do {
            System.out.println("\n--- Menú ---");
            for (int i = 0; i < opciones.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, opciones.get(i).getDescripcion());
            }
            System.out.println("0) Salir");
            seleccion = consola.leerEnteroEnRango("Opción: ", 0, opciones.size());
            if (seleccion == 0) {
                System.out.println("Fin del programa.");
            } else {
                ejecutarOpcion(opciones.get(seleccion - 1), usuario);
            }
        } while (seleccion != 0);
    }

    /** UC-01: pide las credenciales hasta MAX_INTENTOS veces. Devuelve null si no se logra ingresar. */
    private Usuario iniciarSesion() {
        for (int intento = 1; intento <= ControladorSesion.MAX_INTENTOS; intento++) {
            String nombre = consola.leerTexto("Usuario: ");
            String contrasena = consola.leerTexto("Contraseña: ");
            try {
                return controladores.sesion().iniciarSesion(nombre, contrasena);
            } catch (CredencialesInvalidasException e) {
                System.out.printf("%s Intento %d de %d.%n", e.getMessage(), intento,
                        ControladorSesion.MAX_INTENTOS);
            } catch (InventarioException e) {
                System.out.println("Error: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    private void ejecutarOpcion(OpcionMenu opcion, Usuario usuario) {
        try {
            switch (opcion) {
                case CONSULTAR_STOCK:
                    listarProductos(controladores.stock().listar());
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
                    listarProductos(controladores.stock().ordenarPorCriticidad(controladores.stock().listar()));
                    break;
                case REGISTRAR_MOVIMIENTO:
                    registrarMovimiento(usuario);
                    break;
                case VER_HISTORIAL_SESION:
                    verHistorialSesion();
                    break;
                default:
                    System.out.println("Opción disponible en la interfaz gráfica.");
            }
        } catch (InventarioException e) {
            System.out.println("  No se pudo completar la operación: " + e.getMessage());
        }
    }

    private void listarProductos(List<Producto> productos) {
        for (Producto p : productos) {
            System.out.printf("  [%d] %-25s %-14s stock: %3d / mínimo: %3d  %s%n", p.getId(), p.getNombre(),
                    p.getCategoria(), p.getStockActual(), p.getStockMinimo(), p.getEstadoStock());
        }
    }

    private void buscarProducto() throws InventarioException {
        String texto = consola.leerTexto("Nombre (o parte del nombre): ");
        if (texto.isEmpty()) {
            System.out.println("  Debe ingresar un texto para buscar.");
            return;
        }
        List<Producto> encontrados = controladores.stock().buscar(texto);
        if (encontrados.isEmpty()) {
            System.out.println("  No se encontraron productos que coincidan con \"" + texto + "\".");
        } else {
            listarProductos(encontrados);
        }
    }

    /** UC-05: muestra las ofertas de los proveedores de un producto ordenadas por el criterio elegido. */
    private void compararProveedores() throws InventarioException {
        Producto producto = controladores.stock().buscarPorId(consola.leerEntero("ID del producto: "));
        int criterio = consola.leerEnteroEnRango("Ordenar por: 1) precio  2) plazo de entrega: ", 1, 2);
        List<ProductoProveedor> ofertas = controladores.proveedores().compararOfertas(producto, criterio - 1);
        if (ofertas.isEmpty()) {
            System.out.println("  No hay proveedores registrados para " + producto.getNombre() + ".");
            return;
        }
        System.out.println("  Ofertas para " + producto.getNombre() + ":");
        for (ProductoProveedor o : ofertas) {
            System.out.printf("   %-30s $ %10.2f   entrega: %2d días   %s%n", o.getProveedor().getNombre(),
                    o.getPrecio(), o.getPlazoEntregaDias(), o.isDisponible() ? "disponible" : "no disponible");
        }
    }

    /** UC-07: muestra la cola FIFO de pedidos pendientes en el orden en que se atenderán. */
    private void verColaPedidos() throws InventarioException {
        ColaEnlazada<PedidoReposicion> cola = controladores.pedidos().colaPendientes();
        if (cola.estaVacia()) {
            System.out.println("  No hay pedidos de reposición pendientes.");
            return;
        }
        System.out.println("  Pedidos pendientes (" + cola.tamanio() + "), en orden de atención:");
        int posicion = 1;
        for (PedidoReposicion pedido : cola) {
            System.out.printf("   %d. %s (generado el %s)%n", posicion++, pedido, pedido.getFechaGeneracion().toLocalDate());
        }
        System.out.println("  Próximo a gestionar: " + cola.verFrente().getProducto().getNombre());
    }

    /** UC-08: registra una entrada o salida. Si corresponde, dispara UC-09 / UC-10 dentro de la misma transacción. */
    private void registrarMovimiento(Usuario usuario) throws InventarioException {
        int idProducto = consola.leerEntero("ID del producto: ");
        TipoMovimiento tipo = leerTipoMovimiento();
        int cantidad = consola.leerEntero("Cantidad: ");
        ResultadoMovimiento resultado = controladores.movimientos().registrar(usuario, idProducto, tipo, cantidad);
        System.out.printf("  Movimiento registrado. Stock resultante: %d.%n", resultado.getStockResultante());
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
        List<MovimientoStock> historial = controladores.movimientos().getHistorialSesion();
        if (historial.isEmpty()) {
            System.out.println("  Todavía no se registraron movimientos en esta sesión.");
            return;
        }
        System.out.println("  Movimientos de esta sesión (del más reciente al más antiguo):");
        for (MovimientoStock m : historial) {
            System.out.printf("   %-8s %3d x %s%n", m.getTipo(), m.getCantidad(), m.getProducto().getNombre());
        }
    }
}
