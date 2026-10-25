package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorSesion;
import com.sabordelsur.inventario.excepciones.CredencialesInvalidasException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.ConexionBD;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** UC-01: pantalla de inicio de sesión. Permite hasta tres intentos antes de cerrar la aplicación. */
public class VentanaLogin extends JDialog {

    private static final long serialVersionUID = 1L;

    private final ControladorSesion controlador;
    private final JTextField campoUsuario = new JTextField(18);
    private final JPasswordField campoContrasena = new JPasswordField(18);
    private final JLabel mensaje = new JLabel(" ");
    private int intentos = 0;
    private Usuario usuario;

    public VentanaLogin(ControladorSesion controlador) {
        super((java.awt.Frame) null, "Iniciar sesión - Sabor Sur", true);
        this.controlador = controlador;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setBackground(Estilo.VERDE);
        encabezado.setBorder(Estilo.margen(16));
        JLabel nombre = new JLabel("Distribuidora Sabor Sur S.R.L.");
        nombre.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        nombre.setForeground(Color.WHITE);
        JLabel sistema = new JLabel("Sistema de Gestión de Inventario y Reposición");
        sistema.setForeground(new Color(0xD8, 0xEE, 0xE2));
        encabezado.add(nombre);
        encabezado.add(sistema);

        campoUsuario.setName("login.usuario");
        campoContrasena.setName("login.contrasena");
        mensaje.setName("login.mensaje");
        mensaje.setForeground(new Color(0xB0, 0x20, 0x20));

        JButton ingresar = Estilo.boton("Ingresar", "login.ingresar");
        JButton salir = Estilo.boton("Salir", "login.salir");
        ingresar.addActionListener(e -> intentarIngreso());
        salir.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(ingresar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(salir);
        botones.add(ingresar);

        Formulario formulario = new Formulario()
                .agregar("Usuario:", campoUsuario)
                .agregar("Contraseña:", campoContrasena)
                .agregarAncho(mensaje)
                .agregarAncho(botones);
        formulario.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        JLabel pie = Estilo.ayuda(ConexionBD.estaDisponible()
                ? "  Base de datos conectada."
                : "  Sin conexión con la base de datos: revise config/sabor_sur.properties.");
        pie.setName("login.conexion");
        pie.setBorder(BorderFactory.createEmptyBorder(0, 8, 10, 8));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(encabezado, BorderLayout.NORTH);
        getContentPane().add(formulario, BorderLayout.CENTER);
        getContentPane().add(pie, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void intentarIngreso() {
        try {
            usuario = controlador.iniciarSesion(campoUsuario.getText(), new String(campoContrasena.getPassword()));
            dispose();
        } catch (CredencialesInvalidasException e) {
            intentos++;
            campoContrasena.setText("");
            if (intentos >= ControladorSesion.MAX_INTENTOS) {
                mostrarMensaje("Se superó la cantidad de intentos permitidos.");
                dispose();
            } else {
                mostrarMensaje(e.getMessage() + " Intento " + intentos + " de " + ControladorSesion.MAX_INTENTOS + ".");
            }
        } catch (InventarioException e) {
            mostrarMensaje(e.getMessage());
        }
    }

    /** Muestra el mensaje en varias líneas si hace falta y ajusta el alto de la ventana. */
    private void mostrarMensaje(String texto) {
        mensaje.setText("<html><body style='width:280px'>" + texto + "</body></html>");
        pack();
    }

    /** Muestra la ventana (modal) y devuelve el usuario autenticado, o null si no se pudo ingresar. */
    public Usuario solicitarIngreso() {
        setVisible(true);
        return usuario;
    }
}
