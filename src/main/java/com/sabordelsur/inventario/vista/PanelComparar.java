package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorProveedores;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

/** UC-05: compara las ofertas de los proveedores de un producto por precio o por plazo de entrega. */
public class PanelComparar extends PanelBase {

    private static final long serialVersionUID = 1L;

    private final ControladorProveedores controlador;
    private final JComboBox<Producto> comboProducto = new JComboBox<>();
    private final JComboBox<String> comboCriterio = new JComboBox<>(ControladorProveedores.CRITERIOS);
    private final JLabel mejor = new JLabel(" ");
    private final ModeloTabla<ProductoProveedor> modelo = new ModeloTabla<ProductoProveedor>(
            "Proveedor", "Contacto", "Precio ($)", "Plazo (días)", "Disponible") {
        private static final long serialVersionUID = 1L;

        @Override
        protected Object valor(ProductoProveedor o, int columna) {
            switch (columna) {
                case 0:
                    return o.getProveedor().getNombre();
                case 1:
                    return o.getProveedor().getContacto();
                case 2:
                    return o.getPrecio();
                case 3:
                    return o.getPlazoEntregaDias();
                default:
                    return o.isDisponible() ? "Sí" : "No";
            }
        }
    };
    private ProductoProveedor mejorOferta;
    private boolean cargando;

    public PanelComparar(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        this.controlador = controladores.proveedores();
        comboProducto.setName("comp.producto");
        comboCriterio.setName("comp.criterio");
        comboProducto.addActionListener(e -> comparar());
        comboCriterio.addActionListener(e -> comparar());
        mejor.setName("comp.mejor");
        mejor.setFont(Estilo.SUBTITULO);
        mejor.setForeground(Estilo.VERDE);

        JTable tabla = new JTable(modelo);
        tabla.setName("comp.tabla");
        Estilo.formatearTabla(tabla);
        DefaultTableCellRenderer resaltado = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int f, int c) {
                Component celda = super.getTableCellRendererComponent(t, v, sel, foco, f, c);
                setHorizontalAlignment(v instanceof Number ? RIGHT : LEFT);
                if (v instanceof Double) {
                    setText(String.format("%,.2f", (Double) v));
                }
                if (!sel) {
                    celda.setBackground(modelo.getFila(f) == mejorOferta ? Estilo.VERDE_CLARO : Color.WHITE);
                }
                return celda;
            }
        };
        tabla.setDefaultRenderer(Object.class, resaltado);
        tabla.setDefaultRenderer(Double.class, resaltado);
        tabla.setDefaultRenderer(Integer.class, resaltado);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Producto:"));
        filtros.add(comboProducto);
        filtros.add(new JLabel("   Ordenar por:"));
        filtros.add(comboCriterio);

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.setOpaque(false);
        norte.add(Estilo.titulo("Comparar condiciones de proveedores"), BorderLayout.NORTH);
        norte.add(filtros, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(mejor, BorderLayout.NORTH);
        sur.add(getEtiquetaEstado(), BorderLayout.SOUTH);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Comparar proveedores";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        cargando = true;
        Object seleccionado = comboProducto.getSelectedItem();
        comboProducto.removeAllItems();
        for (Producto p : controladores.stock().buscar("")) {
            comboProducto.addItem(p);
            if (seleccionado instanceof Producto && ((Producto) seleccionado).getId() == p.getId()) {
                comboProducto.setSelectedItem(p);
            }
        }
        cargando = false;
        comparar();
    }

    private void comparar() {
        Producto producto = (Producto) comboProducto.getSelectedItem();
        if (cargando || producto == null) {
            return;
        }
        try {
            List<ProductoProveedor> ofertas = controlador.compararOfertas(producto, comboCriterio.getSelectedIndex());
            mejorOferta = controlador.mejorOferta(ofertas);
            modelo.setFilas(ofertas);
            if (ofertas.isEmpty()) {
                mejor.setText("No hay proveedores registrados para " + producto.getNombre() + ".");
            } else if (mejorOferta == null) {
                mejor.setText("Ningún proveedor tiene el producto disponible en este momento.");
            } else {
                mejor.setText(String.format("Mejor opción por %s: %s ($ %,.2f, %d días)",
                        comboCriterio.getSelectedIndex() == 1 ? "plazo" : "precio",
                        mejorOferta.getProveedor().getNombre(), mejorOferta.getPrecio(), mejorOferta.getPlazoEntregaDias()));
            }
            informar(Estilo.plural(ofertas.size(), "oferta", "ofertas") + " para " + producto.getNombre() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }
}
