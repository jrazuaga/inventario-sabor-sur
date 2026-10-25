package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.modelo.OpcionMenu;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.ConexionBD;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal. Arma una pestaña por cada funcionalidad que el rol del usuario tiene permitida
 * (RF-08): el operador ve stock y movimientos; el administrador, además, pedidos, comparación de
 * proveedores, gestión de productos, proveedores y usuarios, y reportes.
 */
public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final FabricaControladores controladores;
    private final Usuario usuario;
    private final Runnable alCerrarSesion;
    private final JTabbedPane pestanas = new JTabbedPane();
    private final List<PanelBase> paneles = new ArrayList<>();

    public VentanaPrincipal(FabricaControladores controladores, Usuario usuario, Runnable alCerrarSesion) {
        super("Sabor Sur - Inventario y Reposición");
        this.controladores = controladores;
        this.usuario = usuario;
        this.alCerrarSesion = alCerrarSesion;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));

        pestanas.setName("pestanas");
        agregarPaneles();
        pestanas.addChangeListener(e -> {
            int indice = pestanas.getSelectedIndex();
            if (indice >= 0) {
                paneles.get(indice).refrescar();
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(crearEncabezado(), BorderLayout.NORTH);
        getContentPane().add(pestanas, BorderLayout.CENTER);
        getContentPane().add(crearBarraEstado(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        if (!paneles.isEmpty()) {
            paneles.get(0).refrescar();
        }
    }

    /** Cada opción del menú habilitada para el rol se traduce en una pestaña. */
    private void agregarPaneles() {
        if (usuario.puedeAcceder(OpcionMenu.CONSULTAR_STOCK)) {
            agregar(new PanelStock(controladores, usuario));
        }
        if (usuario.puedeAcceder(OpcionMenu.REGISTRAR_MOVIMIENTO)) {
            agregar(new PanelMovimientos(controladores, usuario));
        }
        if (usuario.puedeAcceder(OpcionMenu.VER_COLA_PEDIDOS)) {
            agregar(new PanelPedidos(controladores, usuario));
        }
        if (usuario.puedeAcceder(OpcionMenu.COMPARAR_PROVEEDORES)) {
            agregar(new PanelComparar(controladores, usuario));
        }
        if (usuario.puedeAcceder(OpcionMenu.GESTIONAR_PRODUCTOS)) {
            agregar(new PanelProductos(controladores, usuario));
        }
        if (usuario.puedeAcceder(OpcionMenu.GESTIONAR_PROVEEDORES)) {
            agregar(new PanelProveedores(controladores, usuario));
        }
        if (usuario.puedeAcceder(OpcionMenu.GESTIONAR_USUARIOS)) {
            agregar(new PanelUsuarios(controladores, usuario));
        }
        if (usuario.puedeAcceder(OpcionMenu.REPORTES)) {
            agregar(new PanelReportes(controladores, usuario));
        }
    }

    private void agregar(PanelBase panel) {
        paneles.add(panel);
        pestanas.addTab(panel.getTitulo(), panel);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilo.VERDE);
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel titulo = new JLabel("Distribuidora Sabor Sur  ·  Inventario y Reposición");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);

        JLabel sesion = new JLabel("Usuario: " + usuario.getNombreUsuario() + "  (" + usuario.getRol() + ")   ");
        sesion.setName("principal.sesion");
        sesion.setForeground(Color.WHITE);
        JButton cerrar = Estilo.boton("Cerrar sesión", "principal.cerrarSesion");
        cerrar.addActionListener(e -> cerrarSesion());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);
        derecha.add(sesion);
        derecha.add(cerrar);

        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(derecha, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xC9, 0xD6, 0xCF)));
        String url = ConexionBD.getUrl();
        int corte = url.indexOf('?');
        barra.add(Estilo.ayuda("Base de datos: " + (corte > 0 ? url.substring(0, corte) : url)));
        barra.add(Estilo.ayuda("|  Bitácora: " + controladores.reportes().getArchivoBitacora()));
        return barra;
    }

    private void cerrarSesion() {
        controladores.sesion().cerrarSesion(usuario);
        dispose();
        alCerrarSesion.run();
    }
}
