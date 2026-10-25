package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorMovimientos;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Producto;
import com.sabordelsur.inventario.modelo.ResultadoMovimiento;
import com.sabordelsur.inventario.modelo.TipoMovimiento;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/** UC-08: registro de entradas y salidas, con el historial de la sesión (pila) y los últimos movimientos. */
public class PanelMovimientos extends PanelBase {

    private static final long serialVersionUID = 1L;
    private static final int CANTIDAD_RECIENTES = 15;

    private final ControladorMovimientos controlador;
    private final JComboBox<Producto> comboProducto = new JComboBox<>();
    private final JRadioButton opcionEntrada = new JRadioButton("Entrada");
    private final JRadioButton opcionSalida = new JRadioButton("Salida", true);
    private final JTextField campoCantidad = new JTextField(8);
    private final JLabel stockSeleccionado = new JLabel(" ");
    private final TablaMovimientos modeloSesion = new TablaMovimientos();
    private final TablaMovimientos modeloRecientes = new TablaMovimientos();

    public PanelMovimientos(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        this.controlador = controladores.movimientos();

        comboProducto.setName("mov.producto");
        opcionEntrada.setName("mov.entrada");
        opcionSalida.setName("mov.salida");
        campoCantidad.setName("mov.cantidad");
        stockSeleccionado.setName("mov.stockSeleccionado");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(opcionEntrada);
        grupo.add(opcionSalida);
        opcionEntrada.setOpaque(false);
        opcionSalida.setOpaque(false);
        comboProducto.addActionListener(e -> mostrarStockSeleccionado());

        JPanel tipos = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tipos.setOpaque(false);
        tipos.add(opcionEntrada);
        tipos.add(opcionSalida);

        JButton registrar = Estilo.boton("Registrar movimiento", "mov.registrar");
        registrar.addActionListener(e -> registrar());

        Formulario formulario = new Formulario()
                .agregar("Producto:", comboProducto)
                .agregar("", stockSeleccionado)
                .agregar("Tipo:", tipos)
                .agregar("Cantidad:", campoCantidad)
                .agregarAncho(registrar)
                .cerrar();
        formulario.setBorder(Estilo.seccion("Nuevo movimiento"));
        formulario.setPreferredSize(new Dimension(360, 300));

        JPanel tablas = new JPanel(new GridLayout(2, 1, 0, 10));
        tablas.setOpaque(false);
        tablas.add(envolver("Movimientos de esta sesión (el último registrado queda arriba)", modeloSesion, "mov.tablaSesion"));
        tablas.add(envolver("Últimos movimientos registrados en la base", modeloRecientes, "mov.tablaRecientes"));

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formulario, tablas);
        division.setBorder(null);
        division.setDividerLocation(370);

        add(Estilo.titulo("Registrar movimiento de stock"), BorderLayout.NORTH);
        add(division, BorderLayout.CENTER);
        add(getEtiquetaEstado(), BorderLayout.SOUTH);
    }

    private JPanel envolver(String titulo, TablaMovimientos modelo, String nombre) {
        JTable tabla = new JTable(modelo);
        tabla.setName(nombre);
        Estilo.formatearTabla(tabla);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(Estilo.seccion(titulo));
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public String getTitulo() {
        return "Movimientos";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        Object seleccionado = comboProducto.getSelectedItem();
        comboProducto.removeAllItems();
        for (Producto p : controladores.stock().buscar("")) {
            comboProducto.addItem(p);
            if (seleccionado instanceof Producto && ((Producto) seleccionado).getId() == p.getId()) {
                comboProducto.setSelectedItem(p);
            }
        }
        modeloSesion.setFilas(controlador.getHistorialSesion());
        modeloRecientes.setFilas(controlador.recientes(CANTIDAD_RECIENTES));
        mostrarStockSeleccionado();
    }

    private void mostrarStockSeleccionado() {
        Producto p = (Producto) comboProducto.getSelectedItem();
        stockSeleccionado.setText(p == null ? " "
                : "Stock actual: " + p.getStockActual() + "  ·  mínimo: " + p.getStockMinimo());
    }

    private void registrar() {
        try {
            Producto producto = (Producto) comboProducto.getSelectedItem();
            if (producto == null) {
                throw new DatoInvalidoException("Seleccione un producto.");
            }
            int cantidad = leerEntero(campoCantidad.getText(), "Cantidad");
            TipoMovimiento tipo = opcionEntrada.isSelected() ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA;

            ResultadoMovimiento resultado = controlador.registrar(usuario, producto.getId(), tipo, cantidad);
            campoCantidad.setText("");
            cargarDatos();

            StringBuilder mensaje = new StringBuilder("Movimiento registrado. Stock de ")
                    .append(producto.getNombre()).append(": ").append(resultado.getStockResultante()).append('.');
            if (resultado.isPedidoGenerado()) {
                mensaje.append(" Quedó por debajo del mínimo: se generó un pedido de reposición.");
            } else if (resultado.isPedidoResuelto()) {
                mensaje.append(" Stock recompuesto: se resolvió el pedido de reposición pendiente.");
            }
            informar(mensaje.toString());
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }
}
