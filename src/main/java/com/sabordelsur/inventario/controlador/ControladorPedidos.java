package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.estructuras.ColaEnlazada;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.PedidoReposicion;
import com.sabordelsur.inventario.persistencia.PedidoReposicionDAO;

/** UC-07: arma la cola FIFO de pedidos pendientes, en el orden en que se van a gestionar. */
public class ControladorPedidos {

    private final PedidoReposicionDAO pedidoDAO;

    public ControladorPedidos(PedidoReposicionDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    public ColaEnlazada<PedidoReposicion> colaPendientes() throws PersistenciaException {
        ColaEnlazada<PedidoReposicion> cola = new ColaEnlazada<>();
        for (PedidoReposicion pedido : pedidoDAO.listarPendientes()) {
            cola.encolar(pedido);
        }
        return cola;
    }
}
