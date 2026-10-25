package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.estructuras.ColaEnlazada;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.PedidoReposicion;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** UC-07: cola FIFO de pedidos de reposición pendientes, en el orden en que se van a gestionar. */
public class PanelPedidos extends PanelBase {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ModeloTabla<PedidoReposicion> modelo = new ModeloTabla<PedidoReposicion>(
            "Orden", "Pedido", "Producto", "Stock actual", "Stock mínimo", "Generado el") {
        private static final long serialVersionUID = 1L;

        /** La columna "Orden" es la posición en la cola, que coincide con el número de fila. */
        @Override
        public Object getValueAt(int fila, int columna) {
            return columna == 0 ? fila + 1 : super.getValueAt(fila, columna);
        }

        @Override
        protected Object valor(PedidoReposicion p, int columna) {
            switch (columna) {
                case 1:
                    return "#" + p.getId();
                case 2:
                    return p.getProducto().getNombre();
                case 3:
                    return p.getProducto().getStockActual();
                case 4:
                    return p.getProducto().getStockMinimo();
                default:
                    return p.getFechaGeneracion().format(FORMATO);
            }
        }
    };
    private final JLabel proximo = new JLabel(" ");

    public PanelPedidos(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        JTable tabla = new JTable(modelo);
        tabla.setName("pedidos.tabla");
        Estilo.formatearTabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        proximo.setName("pedidos.proximo");
        proximo.setFont(Estilo.SUBTITULO);

        JButton actualizar = Estilo.boton("Actualizar", "pedidos.actualizar");
        actualizar.addActionListener(e -> refrescar());

        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        norte.add(Estilo.titulo("Cola de pedidos de reposición pendientes"), BorderLayout.WEST);
        norte.add(actualizar, BorderLayout.EAST);
        norte.add(Estilo.ayuda("Los pedidos se generan solos cuando una salida deja el stock bajo el mínimo y se "
                + "resuelven cuando una entrada lo recompone. Se atienden en orden de llegada."), BorderLayout.SOUTH);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(proximo, BorderLayout.NORTH);
        sur.add(getEtiquetaEstado(), BorderLayout.SOUTH);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Pedidos de reposición";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        ColaEnlazada<PedidoReposicion> cola = controladores.pedidos().colaPendientes();
        List<PedidoReposicion> filas = new ArrayList<>();
        for (PedidoReposicion pedido : cola) {
            filas.add(pedido);
        }
        modelo.setFilas(filas);
        proximo.setText(cola.estaVacia() ? "No hay pedidos pendientes."
                : "Próximo a gestionar: " + cola.verFrente().getProducto().getNombre());
        informar(Estilo.plural(cola.tamanio(), "pedido pendiente", "pedidos pendientes") + ".");
    }
}
