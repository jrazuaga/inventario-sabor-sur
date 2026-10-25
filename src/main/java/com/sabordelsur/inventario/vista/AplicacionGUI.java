package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.util.Locale;

/** Punto de entrada de la interfaz gráfica: muestra el inicio de sesión y luego la ventana principal. */
public final class AplicacionGUI {

    private final FabricaControladores controladores = new FabricaControladores();

    private AplicacionGUI() {
    }

    public static void iniciar() {
        // Textos estándar de Swing (botones "Aceptar", "Sí", "Cancelar") en español, sea cual sea el idioma del equipo.
        Locale.setDefault(new Locale("es", "AR"));
        JComponent.setDefaultLocale(Locale.getDefault());
        Estilo.aplicarAspecto();
        SwingUtilities.invokeLater(() -> new AplicacionGUI().mostrarLogin());
    }

    private void mostrarLogin() {
        Usuario usuario = new VentanaLogin(controladores.sesion()).solicitarIngreso();
        if (usuario == null) {
            System.exit(0);
        }
        new VentanaPrincipal(controladores, usuario, this::mostrarLogin).setVisible(true);
    }
}
