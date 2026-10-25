package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorUsuarios;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Rol;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

/** UC-06: alta, cambio de rol, restablecimiento de contraseña y baja de usuarios. */
public class PanelUsuarios extends PanelBase {

    private static final long serialVersionUID = 1L;

    private final ControladorUsuarios controlador;
    private final ModeloTabla<Usuario> modelo = new ModeloTabla<Usuario>("ID", "Usuario", "Rol") {
        private static final long serialVersionUID = 1L;

        @Override
        protected Object valor(Usuario u, int columna) {
            return columna == 0 ? u.getId() : columna == 1 ? u.getNombreUsuario() : u.getRol();
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextField campoNombre = new JTextField(16);
    private final JPasswordField campoContrasena = new JPasswordField(16);
    private final JComboBox<Rol> comboRol = new JComboBox<>(Rol.values());

    public PanelUsuarios(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        this.controlador = controladores.usuarios();

        tabla.setName("usr.tabla");
        Estilo.formatearTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        campoNombre.setName("usr.nombre");
        campoContrasena.setName("usr.contrasena");
        comboRol.setName("usr.rol");
        comboRol.setSelectedItem(Rol.OPERADOR);

        JButton crear = Estilo.boton("Crear usuario", "usr.crear");
        crear.addActionListener(e -> crear());
        Formulario alta = new Formulario()
                .agregar("Usuario:", campoNombre)
                .agregar("Contraseña:", campoContrasena)
                .agregar("Rol:", comboRol)
                .agregarAncho(Estilo.ayudaMultilinea("Mínimo " + ControladorUsuarios.LARGO_MINIMO_CONTRASENA
                        + " caracteres. Se guarda solo su hash SHA-256.", 300))
                .agregarAncho(crear);
        alta.setBorder(Estilo.seccion("Nuevo usuario"));

        JButton cambiarRol = Estilo.boton("Cambiar rol", "usr.cambiarRol");
        JButton restablecer = Estilo.boton("Restablecer contraseña", "usr.restablecer");
        JButton eliminar = Estilo.boton("Eliminar", "usr.eliminar");
        cambiarRol.addActionListener(e -> cambiarRol());
        restablecer.addActionListener(e -> restablecer());
        eliminar.addActionListener(e -> eliminar());
        JPanel acciones = new JPanel(new GridLayout(3, 1, 0, 6));
        acciones.setOpaque(false);
        acciones.setBorder(Estilo.seccion("Usuario seleccionado"));
        acciones.add(cambiarRol);
        acciones.add(restablecer);
        acciones.add(eliminar);

        JPanel derecha = new JPanel(new BorderLayout(0, 10));
        derecha.setOpaque(false);
        derecha.add(alta, BorderLayout.NORTH);
        JPanel contenedorAcciones = new JPanel(new BorderLayout());
        contenedorAcciones.setOpaque(false);
        contenedorAcciones.add(acciones, BorderLayout.NORTH);
        derecha.add(contenedorAcciones, BorderLayout.CENTER);
        derecha.setPreferredSize(new Dimension(380, 300));

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tabla), derecha);
        division.setResizeWeight(1);
        division.setBorder(null);

        add(Estilo.titulo("Gestión de usuarios y roles"), BorderLayout.NORTH);
        add(division, BorderLayout.CENTER);
        add(getEtiquetaEstado(), BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Usuarios";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        modelo.setFilas(controlador.listar());
    }

    private Usuario seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            informar("Seleccione un usuario en la tabla.");
            return null;
        }
        return modelo.getFila(fila);
    }

    private void crear() {
        try {
            String nombre = campoNombre.getText();
            controlador.crear(nombre, new String(campoContrasena.getPassword()), (Rol) comboRol.getSelectedItem(), usuario);
            campoNombre.setText("");
            campoContrasena.setText("");
            cargarDatos();
            informar("Usuario creado: " + nombre.trim() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void cambiarRol() {
        Usuario elegido = seleccionado();
        if (elegido == null) {
            return;
        }
        Rol nuevo = elegido.getRol() == Rol.ADMINISTRADOR ? Rol.OPERADOR : Rol.ADMINISTRADOR;
        if (!confirmar("¿Cambiar el rol de " + elegido.getNombreUsuario() + " a " + nuevo + "?")) {
            return;
        }
        try {
            controlador.cambiarRol(elegido, nuevo, usuario);
            cargarDatos();
            informar("Rol actualizado: " + elegido.getNombreUsuario() + " ahora es " + nuevo + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void restablecer() {
        Usuario elegido = seleccionado();
        if (elegido == null) {
            return;
        }
        JPasswordField nueva = new JPasswordField(16);
        int opcion = JOptionPane.showConfirmDialog(this, nueva,
                "Nueva contraseña para " + elegido.getNombreUsuario(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            controlador.restablecerContrasena(elegido, new String(nueva.getPassword()), usuario);
            informar("Contraseña restablecida para " + elegido.getNombreUsuario() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        Usuario elegido = seleccionado();
        if (elegido == null || !confirmar("¿Eliminar el usuario " + elegido.getNombreUsuario() + "?")) {
            return;
        }
        try {
            controlador.eliminar(elegido, usuario);
            cargarDatos();
            informar("Usuario eliminado: " + elegido.getNombreUsuario() + ".");
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }
}
