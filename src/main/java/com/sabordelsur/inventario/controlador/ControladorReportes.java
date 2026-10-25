package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.archivos.ReporteMovimientosCSV;
import com.sabordelsur.inventario.archivos.ReporteStockCSV;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.CrudDAO;
import com.sabordelsur.inventario.persistencia.MovimientoStockDAO;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Exportación de reportes a archivos CSV y consulta de la bitácora de actividad. */
public class ControladorReportes {

    public static final Path CARPETA_POR_DEFECTO = Paths.get("reportes");
    public static final int MAXIMO_MOVIMIENTOS = 1000;
    private static final DateTimeFormatter SELLO = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final CrudDAO<Producto> productoDAO;
    private final MovimientoStockDAO movimientoDAO;
    private final Bitacora bitacora;
    private final Path carpeta;

    public ControladorReportes(CrudDAO<Producto> productoDAO, MovimientoStockDAO movimientoDAO,
                               Bitacora bitacora, Path carpeta) {
        this.productoDAO = productoDAO;
        this.movimientoDAO = movimientoDAO;
        this.bitacora = bitacora;
        this.carpeta = carpeta;
    }

    /** Genera reportes/stock-AAAAMMDD-HHMMSS.csv y devuelve la ruta del archivo creado. */
    public Path exportarStock(Usuario actor) throws InventarioException {
        Path destino = carpeta.resolve("stock-" + LocalDateTime.now().format(SELLO) + ".csv");
        int filas = new ReporteStockCSV().exportar(productoDAO.listarTodos(), destino);
        bitacora.registrar(actor.getNombreUsuario(), "Exportó el reporte de stock (" + filas + " productos): " + destino);
        return destino;
    }

    /** Genera reportes/movimientos-AAAAMMDD-HHMMSS.csv con los últimos movimientos registrados. */
    public Path exportarMovimientos(Usuario actor) throws InventarioException {
        Path destino = carpeta.resolve("movimientos-" + LocalDateTime.now().format(SELLO) + ".csv");
        int filas = new ReporteMovimientosCSV().exportar(movimientoDAO.listarRecientes(MAXIMO_MOVIMIENTOS), destino);
        bitacora.registrar(actor.getNombreUsuario(), "Exportó el reporte de movimientos (" + filas + " filas): " + destino);
        return destino;
    }

    public List<String> leerBitacora(int lineas) throws InventarioException {
        return bitacora.leerUltimas(lineas);
    }

    public Path getArchivoBitacora() {
        return bitacora.getArchivo();
    }
}
