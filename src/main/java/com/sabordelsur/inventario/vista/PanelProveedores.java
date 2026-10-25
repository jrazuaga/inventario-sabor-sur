package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorProveedores;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ProductoProveedor;
import com.sabordelsur.inventario.modelo.Proveedor;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

/** UC-03 (proveedores) y UC-04 (ofertas de cada proveedor para los productos). */
public class PanelProveedores extends PanelBase {

    private static final long serialVersionUID = 1L;

    private final ControladorProveedores controlador;

    private final ModeloTabla<Proveedor> modeloProveedores = new ModeloTabla<Proveedor>("ID", "Proveedor", "Contacto") {
        private static final long serialVersionUID = 1L;

        @Override
        protected Object valor(Proveedor p, int columna) {
            return columna == 0 ? p.getId() : columna == 1 ? p.getNombre() : p.getContacto();
        }
    };
    private final ModeloTabla<ProductoProveedor> modeloOfertas =
            new ModeloTabla<ProductoProveedor>("Producto", "Precio ($)", "Plazo (días)", "Disponible") {
        private static final long serialVersionUID = 1L;

        @Override
        protected Object valor(ProductoProveedor o, int columna) {
            switch (columna) {
                case 0:
                    return o.getProducto().getNombre();
                case 1:
                    return o.getPrecio();
                case 2:
                    return o.getPlazoEntregaDias();
                default:
                    return o.isDisponible() ? "Sí" : "No";
            }
        }
    };

    private final JTable tablaProveedores = new JTable(modeloProveedores);
    private final JTable tablaOfertas = new JTable(modeloOfertas);
    private final JLabel etiquetaId = new JLabel("(nuevo)");
    private final JTextField campoNombre = new JTextField(20);
    private final JTextField campoContacto = new JTextField(20);
    private final JLabel tituloOfertas = new JLabel("Ofertas: seleccione un proveedor");
    private final JComboBox<Producto> comboProducto = new JComboBox<>();
    private final JTextField campoPrecio = new JTextField(8);
    private final JTextField campoPlazo = new JTextField(4);
    private final JCheckBox checkDisponible = new JCheckBox("Disponible", true);
    private Proveedor enEdicion;

    public PanelProveedores(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        this.controlador = controladores.proveedores();

        tablaProveedores.setName("prov.tabla");
        tablaOfertas.setName("prov.tablaOfertas");
        Estilo.formatearTabla(tablaProveedores);
        Estilo.formatearTabla(tablaOfertas);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOfertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.getColumnModel().getColumn(0).setMaxWidth(60);
        tablaProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaProveedores.getSelectedRow() >= 0) {
                editar(modeloProveedores.getFila(tablaProveedores.getSelectedRow()));
            }
        });
        tablaOfertas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaOfertas.getSelectedRow() >= 0) {
                cargarOfertaEnFormulario(modeloOfertas.getFila(tablaOfertas.getSelectedRow()));
            }
        });

        campoNombre.setName("prov.nombre");
        campoContacto.setName("prov.contacto");
        comboProducto.setName("oferta.producto");
        campoPrecio.setName("oferta.precio");
        campoPlazo.setName("oferta.plazo");
        checkDisponible.setName("oferta.disponible");
        checkDisponible.setOpaque(false);
        tituloOfertas.setName("prov.tituloOfertas");
        tituloOfertas.setFont(Estilo.SUBTITULO);

        JButton nuevo = Estilo.boton("Nuevo", "prov.nuevo");
        JButton guardar = Estilo.boton("Guardar", "prov.guardar");
        JButton eliminar = Estilo.boton("Eliminar", "prov.eliminar");
        nuevo.addActionListener(e -> limpiar());
        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());
        JPanel botones = new JPanel(new GridLayout(1, 3, 6, 0));
        botones.setOpaque(false);
        botones.add(nuevo);
        botones.add(guardar);
        botones.add(eliminar);

        Formulario formProveedor = new Formulario()
                .agregar("ID:", etiquetaId)
                .agregar("Nombre:", campoNombre)
                .agregar("Contacto:", campoContacto)
                .agregarAncho(botones)
                .cerrar();
        formProveedor.setBorder(Estilo.seccion("Datos del proveedor"));
        formProveedor.setPreferredSize(new Dimension(380, 190));

        JButton guardarOferta = Estilo.boton("Guardar oferta", "oferta.guardar");
        JButton quitarOferta = Estilo.boton("Quitar oferta", "oferta.quitar");
        guardarOferta.addActionListener(e -> guardarOferta());
        quitarOferta.addActionListener(e -> quitarOferta());
        JPanel botonesOferta = new JPanel(new GridLayout(1, 2, 6, 0));
        botonesOferta.setOpaque(false);
        botonesOferta.add(guardarOferta);
        botonesOferta.add(quitarOferta);

        Formulario formOferta = new Formulario()
                .agregar("Producto:", comboProducto)
                .agregar("Precio ($):", campoPrecio)
                .agregar("Plazo (días):", campoPlazo)
                .agregar("", checkDisponible)
                .agregarAncho(botonesOferta)
                .cerrar();
        formOferta.setBorder(Estilo.seccion("Oferta del proveedor"));
        formOferta.setPreferredSize(new Dimension(380, 220));

        JSplitPane arriba = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tablaProveedores), formProveedor);
        arriba.setResizeWeight(1);
        arriba.setBorder(null);

        JPanel tablaOfertasConTitulo = new JPanel(new BorderLayout(0, 4));
        tablaOfertasConTitulo.setOpaque(false);
        tablaOfertasConTitulo.add(tituloOfertas, BorderLayout.NORTH);
        tablaOfertasConTitulo.add(new JScrollPane(tablaOfertas), BorderLayout.CENTER);
        JSplitPane abajo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablaOfertasConTitulo, formOferta);
        abajo.setResizeWeight(1);
        abajo.setBorder(null);

        JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, arriba, abajo);
        vertical.setResizeWeight(0.45);
        vertical.setBorder(null);

        add(Estilo.titulo("Gestión de proveedores y sus ofertas"), BorderLayout.NORTH);
        add(vertical, BorderLayout.CENTER);
        add(getEtiquetaEstado(), BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Proveedores";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        int idSeleccionado = enEdicion == null ? 0 : enEdicion.getId();
        modeloProveedores.setFilas(controlador.listar());
        comboProducto.removeAllItems();
        for (Producto p : controladores.stock().buscar("")) {
            comboProducto.addItem(p);
        }
        if (idSeleccionado != 0) {
            for (int i = 0; i < modeloProveedores.getRowCount(); i++) {
                if (modeloProveedores.getFila(i).getId() == idSeleccionado) {
                    tablaProveedores.setRowSelectionInterval(i, i);
                }
            }
        }
        cargarOfertas();
    }

    private void editar(Proveedor proveedor) {
        enEdicion = proveedor;
        etiquetaId.setText(String.valueOf(proveedor.getId()));
        campoNombre.setText(proveedor.getNombre());
        campoContacto.setText(proveedor.getContacto() == null ? "" : proveedor.getContacto());
        cargarOfertas();
    }

    private void cargarOfertas() {
        try {
            if (enEdicion == null) {
                modeloOfertas.setFilas(new java.util.ArrayList<>());
                tituloOfertas.setText("Ofertas: seleccione un proveedor");
                return;
            }
            modeloOfertas.setFilas(controlador.ofertasDeProveedor(enEdicion));
            tituloOfertas.setText("Ofertas de " + enEdicion.getNombre() + " (" + modeloOfertas.getRowCount() + ")");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void cargarOfertaEnFormulario(ProductoProveedor oferta) {
        for (int i = 0; i < comboProducto.getItemCount(); i++) {
            if (comboProducto.getItemAt(i).getId() == oferta.getProducto().getId()) {
                comboProducto.setSelectedIndex(i);
            }
        }
        campoPrecio.setText(String.valueOf(oferta.getPrecio()));
        campoPlazo.setText(String.valueOf(oferta.getPlazoEntregaDias()));
        checkDisponible.setSelected(oferta.isDisponible());
    }

    private void limpiar() {
        enEdicion = null;
        tablaProveedores.clearSelection();
        etiquetaId.setText("(nuevo)");
        campoNombre.setText("");
        campoContacto.setText("");
        cargarOfertas();
        campoNombre.requestFocusInWindow();
    }

    private void guardar() {
        try {
            Proveedor proveedor = enEdicion == null ? new Proveedor() : enEdicion;
            proveedor.setNombre(campoNombre.getText());
            proveedor.setContacto(campoContacto.getText());
            boolean alta = proveedor.getId() == 0;
            controlador.guardar(proveedor, usuario);
            enEdicion = proveedor;
            cargarDatos();
            informar((alta ? "Proveedor dado de alta: " : "Cambios guardados: ") + proveedor.getNombre() + ".");
        } catch (InventarioException e) {
            refrescar();
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (enEdicion == null) {
            informar("Seleccione en la tabla el proveedor que desea eliminar.");
            return;
        }
        if (!confirmar("¿Eliminar el proveedor \"" + enEdicion.getNombre() + "\" y todas sus ofertas?")) {
            return;
        }
        try {
            String nombre = enEdicion.getNombre();
            controlador.eliminar(enEdicion, usuario);
            enEdicion = null;
            cargarDatos();
            limpiar();
            informar("Proveedor eliminado: " + nombre + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void guardarOferta() {
        try {
            if (enEdicion == null || enEdicion.getId() == 0) {
                throw new DatoInvalidoException("Primero seleccione (o guarde) el proveedor.");
            }
            Producto producto = (Producto) comboProducto.getSelectedItem();
            ProductoProveedor oferta = new ProductoProveedor(producto, enEdicion,
                    leerImporte(campoPrecio.getText(), "Precio"), leerEntero(campoPlazo.getText(), "Plazo"),
                    checkDisponible.isSelected());
            controlador.guardarOferta(oferta, usuario);
            cargarOfertas();
            informar("Oferta guardada: " + enEdicion.getNombre() + " / " + producto.getNombre() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void quitarOferta() {
        int fila = tablaOfertas.getSelectedRow();
        if (fila < 0) {
            informar("Seleccione en la tabla la oferta que desea quitar.");
            return;
        }
        try {
            ProductoProveedor oferta = modeloOfertas.getFila(fila);
            controlador.quitarOferta(oferta, usuario);
            cargarOfertas();
            informar("Oferta quitada: " + oferta.getProducto().getNombre() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }
}
