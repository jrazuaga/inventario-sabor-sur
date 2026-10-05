package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.excepciones.CantidadInvalidaException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.StockInsuficienteException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias sobre MovimientoStock y sus subclases (UC-08). */
class MovimientoStockTest {

    private final Producto producto = new Producto(1, "Aceite de girasol 5L", null, 12, 10);
    private final Usuario usuario = new Operador(2, "operario1", "x");

    @Test
    void getVariacionStock_esPositiva_paraUnaEntrada() {
        MovimientoStock movimiento = MovimientoStock.crear(TipoMovimiento.ENTRADA, producto, usuario, 15);
        assertEquals(15, movimiento.getVariacionStock());
    }

    @Test
    void getVariacionStock_esNegativa_paraUnaSalida() {
        MovimientoStock movimiento = MovimientoStock.crear(TipoMovimiento.SALIDA, producto, usuario, 6);
        assertEquals(-6, movimiento.getVariacionStock());
    }

    @Test
    void crear_devuelveLaSubclaseQueCorrespondeAlTipo() {
        assertTrue(MovimientoStock.crear(TipoMovimiento.ENTRADA, producto, usuario, 1) instanceof MovimientoEntrada);
        assertTrue(MovimientoStock.crear(TipoMovimiento.SALIDA, producto, usuario, 1) instanceof MovimientoSalida);
        assertEquals(TipoMovimiento.SALIDA,
                MovimientoStock.crear(TipoMovimiento.SALIDA, producto, usuario, 1).getTipo());
    }

    @Test
    void validar_aceptaUnaSalidaQueNoSuperaElStock() {
        MovimientoStock salida = MovimientoStock.crear(TipoMovimiento.SALIDA, producto, usuario, 12);
        assertDoesNotThrow(salida::validar);
    }

    @Test
    void validar_rechazaUnaSalidaMayorAlStockDisponible() {
        MovimientoStock salida = MovimientoStock.crear(TipoMovimiento.SALIDA, producto, usuario, 13);
        StockInsuficienteException e = assertThrows(StockInsuficienteException.class, salida::validar);
        assertEquals(12, e.getDisponible());
        assertEquals(13, e.getSolicitado());
    }

    @Test
    void validar_rechazaCantidadesNoPositivas_enAmbosTipos() {
        MovimientoStock entrada = MovimientoStock.crear(TipoMovimiento.ENTRADA, producto, usuario, 0);
        MovimientoStock salida = MovimientoStock.crear(TipoMovimiento.SALIDA, producto, usuario, -3);
        assertThrows(CantidadInvalidaException.class, entrada::validar);
        assertThrows(CantidadInvalidaException.class, salida::validar);
    }

    @Test
    void lasExcepcionesEspecificasSonInventarioException() {
        MovimientoStock salida = MovimientoStock.crear(TipoMovimiento.SALIDA, producto, usuario, 99);
        assertThrows(InventarioException.class, salida::validar);
    }
}
