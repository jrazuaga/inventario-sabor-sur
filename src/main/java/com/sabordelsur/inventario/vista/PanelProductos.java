package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorProductos;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Categoria;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/** UC-02: alta, modificación y baja de productos, y alta de categorías. */
public class PanelProductos extends PanelBase {

    private static final long serialVersionUID = 1L;

    private final ControladorProductos controlador;
    private final TablaProductos modelo = new TablaProductos();
    private final JTable tabla = new JTable(modelo);
    private final JLabel etiquetaId = new JLabel("(nuevo)");
    private final JTextField campoNombre = new JTextField(20);
    private final JComboBox<Categoria> comboCategoria = new JComboBox<>();
    private final JTextField campoStockInicial = new JTextField("0", 6);
    private final JTextField campoStockMinimo = new JTextField("0", 6);
    private Producto enEdicion;

    public PanelProductos(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        this.controlador = controladores.productos();

        tabla.setName("prod.tabla");
        Estilo.formatearTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultRenderer(Object.class, new RenderizadorEstadoStock(modelo));
        tabla.setDefaultRenderer(Integer.class, new RenderizadorEstadoStock(modelo));
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                editar(modelo.getFila(tabla.getSelectedRow()));
            }
        });

        campoNombre.setName("prod.nombre");
        comboCategoria.setName("prod.categoria");
        campoStockInicial.setName("prod.stockInicial");
        campoStockMinimo.setName("prod.stockMinimo");

        JButton nuevaCategoria = Estilo.boton("Nueva…", "prod.nuevaCategoria");
        JButton renombrarCategoria = Estilo.boton("Renombrar…", "prod.renombrarCategoria");
        nuevaCategoria.addActionListener(e -> crearCategoria());
        renombrarCategoria.addActionListener(e -> renombrarCategoria());
        JPanel botonesCategoria = new JPanel(new GridLayout(1, 2, 4, 0));
        botonesCategoria.setOpaque(false);
        botonesCategoria.add(nuevaCategoria);
        botonesCategoria.add(renombrarCategoria);
        JPanel filaCategoria = new JPanel(new BorderLayout(6, 0));
        filaCategoria.setOpaque(false);
        filaCategoria.add(comboCategoria, BorderLayout.CENTER);
        filaCategoria.add(botonesCategoria, BorderLayout.EAST);

        JButton nuevo = Estilo.boton("Nuevo", "prod.nuevo");
        JButton guardar = Estilo.boton("Guardar", "prod.guardar");
        JButton eliminar = Estilo.boton("Eliminar", "prod.eliminar");
        nuevo.addActionListener(e -> limpiar());
        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());
        JPanel botones = new JPanel(new GridLayout(1, 3, 6, 0));
        botones.setOpaque(false);
        botones.add(nuevo);
        botones.add(guardar);
        botones.add(eliminar);

        Formulario formulario = new Formulario()
                .agregar("ID:", etiquetaId)
                .agregar("Nombre:", campoNombre)
                .agregar("Categoría:", filaCategoria)
                .agregar("Stock inicial:", campoStockInicial)
                .agregar("Stock mínimo:", campoStockMinimo)
                .agregarAncho(Estilo.ayudaMultilinea("El stock inicial solo se carga en el alta: después cambia "
                        + "únicamente con movimientos, para conservar la trazabilidad.", 300))
                .agregarAncho(botones)
                .cerrar();
        formulario.setBorder(Estilo.seccion("Datos del producto"));
        formulario.setPreferredSize(new Dimension(380, 300));

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tabla), formulario);
        division.setBorder(null);
        division.setResizeWeight(1);

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        norte.setOpaque(false);
        norte.add(Estilo.titulo("Gestión de productos y categorías"));

        add(norte, BorderLayout.NORTH);
        add(division, BorderLayout.CENTER);
        add(getEtiquetaEstado(), BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Productos";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        modelo.setFilas(controlador.listar());
        Object categoriaSeleccionada = comboCategoria.getSelectedItem();
        comboCategoria.removeAllItems();
        for (Categoria c : controlador.listarCategorias()) {
            comboCategoria.addItem(c);
            if (categoriaSeleccionada instanceof Categoria && ((Categoria) categoriaSeleccionada).getId() == c.getId()) {
                comboCategoria.setSelectedItem(c);
            }
        }
    }

    private void editar(Producto producto) {
        enEdicion = producto;
        etiquetaId.setText(String.valueOf(producto.getId()));
        campoNombre.setText(producto.getNombre());
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).getId() == producto.getCategoria().getId()) {
                comboCategoria.setSelectedIndex(i);
            }
        }
        campoStockInicial.setText(String.valueOf(producto.getStockActual()));
        campoStockInicial.setEnabled(false);
        campoStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
    }

    private void limpiar() {
        enEdicion = null;
        tabla.clearSelection();
        etiquetaId.setText("(nuevo)");
        campoNombre.setText("");
        campoStockInicial.setText("0");
        campoStockInicial.setEnabled(true);
        campoStockMinimo.setText("0");
        campoNombre.requestFocusInWindow();
    }

    private void guardar() {
        try {
            Producto producto = enEdicion == null ? new Producto() : enEdicion;
            producto.setNombre(campoNombre.getText());
            producto.setCategoria((Categoria) comboCategoria.getSelectedItem());
            if (enEdicion == null) {
                producto.setStockActual(leerEntero(campoStockInicial.getText(), "Stock inicial"));
            }
            producto.setStockMinimo(leerEntero(campoStockMinimo.getText(), "Stock mínimo"));
            boolean alta = producto.getId() == 0;
            controlador.guardar(producto, usuario);
            cargarDatos();
            limpiar();
            informar((alta ? "Producto dado de alta: " : "Cambios guardados: ") + producto.getNombre()
                    + (producto.estaPorDebajoDelMinimo() ? " (queda bajo el mínimo: ver la cola de pedidos)." : "."));
        } catch (InventarioException e) {
            refrescar();
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (enEdicion == null) {
            informar("Seleccione en la tabla el producto que desea eliminar.");
            return;
        }
        if (!confirmar("¿Eliminar el producto \"" + enEdicion.getNombre() + "\" y sus ofertas de proveedores?")) {
            return;
        }
        try {
            controlador.eliminar(enEdicion, usuario);
            String nombre = enEdicion.getNombre();
            cargarDatos();
            limpiar();
            informar("Producto eliminado: " + nombre + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void renombrarCategoria() {
        Categoria categoria = (Categoria) comboCategoria.getSelectedItem();
        if (categoria == null) {
            return;
        }
        String nombre = (String) JOptionPane.showInputDialog(this, "Nuevo nombre para la categoría:",
                "Renombrar categoría", JOptionPane.PLAIN_MESSAGE, null, null, categoria.getNombre());
        if (nombre == null) {
            return;
        }
        try {
            String anterior = categoria.getNombre();
            controlador.renombrarCategoria(categoria, nombre, usuario);
            cargarDatos();
            informar("Categoría renombrada: " + anterior + " pasa a llamarse " + categoria.getNombre() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void crearCategoria() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva categoría:", "Nueva categoría",
                JOptionPane.PLAIN_MESSAGE);
        if (nombre == null) {
            return;
        }
        try {
            Categoria categoria = controlador.crearCategoria(nombre, usuario);
            cargarDatos();
            for (int i = 0; i < comboCategoria.getItemCount(); i++) {
                if (comboCategoria.getItemAt(i).getId() == categoria.getId()) {
                    comboCategoria.setSelectedIndex(i);
                }
            }
            informar("Categoría creada: " + categoria.getNombre() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }
}
