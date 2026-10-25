package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorStock;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.EstadoStock;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ResumenInventario;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/** UC-11: consulta de stock con búsqueda, orden por criticidad y resumen por estado. */
public class PanelStock extends PanelBase {

    private static final long serialVersionUID = 1L;

    private final ControladorStock controlador;
    private final TablaProductos modelo = new TablaProductos();
    private final JTextField campoBusqueda = new JTextField(22);
    private final JCheckBox porCriticidad = new JCheckBox("Ordenar del más crítico al menos crítico");
    private final JLabel[] tarjetas = new JLabel[EstadoStock.values().length];
    private final JLabel criticos = new JLabel(" ");

    public PanelStock(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        this.controlador = controladores.stock();

        campoBusqueda.setName("stock.buscar");
        porCriticidad.setName("stock.criticidad");
        porCriticidad.setOpaque(false);
        JButton buscar = Estilo.boton("Buscar", "stock.botonBuscar");
        JButton todos = Estilo.boton("Mostrar todos", "stock.todos");
        buscar.addActionListener(e -> refrescar());
        campoBusqueda.addActionListener(e -> refrescar());
        todos.addActionListener(e -> {
            campoBusqueda.setText("");
            refrescar();
        });
        porCriticidad.addActionListener(e -> refrescar());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Nombre o parte del nombre:"));
        filtros.add(campoBusqueda);
        filtros.add(buscar);
        filtros.add(todos);
        filtros.add(porCriticidad);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setOpaque(false);
        JLabel titulo = Estilo.titulo("Stock de productos");
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        filtros.setAlignmentX(LEFT_ALIGNMENT);
        norte.add(titulo);
        norte.add(javax.swing.Box.createVerticalStrut(8));
        norte.add(filtros);

        JTable tabla = new JTable(modelo);
        tabla.setName("stock.tabla");
        Estilo.formatearTabla(tabla);
        tabla.setDefaultRenderer(Object.class, new RenderizadorEstadoStock(modelo));
        tabla.setDefaultRenderer(Integer.class, new RenderizadorEstadoStock(modelo));
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(260);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(crearResumen(), BorderLayout.SOUTH);
    }

    private JPanel crearResumen() {
        JPanel fila = new JPanel(new GridLayout(1, tarjetas.length, 10, 0));
        fila.setOpaque(false);
        Color[] fondos = {Estilo.ROJO_SUAVE, Estilo.AMBAR_SUAVE, Estilo.VERDE_CLARO};
        EstadoStock[] estados = EstadoStock.values();
        for (int i = 0; i < tarjetas.length; i++) {
            tarjetas[i] = new JLabel("-", JLabel.CENTER);
            tarjetas[i].setName("stock.resumen." + estados[i].name());
            tarjetas[i].setOpaque(true);
            tarjetas[i].setBackground(fondos[i]);
            tarjetas[i].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            tarjetas[i].setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            fila.add(tarjetas[i]);
        }
        criticos.setName("stock.criticos");
        JPanel resumen = new JPanel(new BorderLayout(0, 6));
        resumen.setOpaque(false);
        resumen.add(fila, BorderLayout.NORTH);
        resumen.add(criticos, BorderLayout.CENTER);
        resumen.add(getEtiquetaEstado(), BorderLayout.SOUTH);
        return resumen;
    }

    @Override
    public String getTitulo() {
        return "Stock";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        List<Producto> productos = controlador.buscar(campoBusqueda.getText());
        if (porCriticidad.isSelected()) {
            productos = controlador.ordenarPorCriticidad(productos);
        }
        modelo.setFilas(productos);

        // El resumen se calcula siempre sobre el catálogo completo, no sobre el resultado de la búsqueda.
        ResumenInventario resumen = controlador.resumir(controlador.listar());
        int[] cantidades = resumen.getCantidadPorEstado();
        EstadoStock[] estados = EstadoStock.values();
        for (int i = 0; i < tarjetas.length; i++) {
            tarjetas[i].setText(estados[i].getDescripcion() + ": " + cantidades[i]);
        }
        StringBuilder texto = new StringBuilder("Más críticos: ");
        Producto[] masCriticos = resumen.getMasCriticos(3);
        for (int i = 0; i < masCriticos.length; i++) {
            texto.append(i > 0 ? ", " : "").append(masCriticos[i].getNombre())
                 .append(" (").append(masCriticos[i].getStockActual()).append('/')
                 .append(masCriticos[i].getStockMinimo()).append(')');
        }
        criticos.setText(texto.toString());
        int aReponer = resumen.getProductosAReponer();
        informar(Estilo.plural(productos.size(), "producto en pantalla", "productos en pantalla") + ". "
                + (aReponer == 0 ? "Ninguno necesita reposición."
                : Estilo.plural(aReponer, "producto necesita", "productos necesitan") + " reposición."));
    }
}
