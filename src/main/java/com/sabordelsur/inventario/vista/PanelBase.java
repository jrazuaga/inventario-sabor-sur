package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.DatoDuplicadoException;
import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.excepciones.RegistroEnUsoException;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Clase abstracta de todas las pestañas de la ventana principal. Define lo que cada pantalla debe
 * implementar (su título y cómo recargar sus datos) y centraliza la forma de mostrar errores, de modo
 * que todas las excepciones del sistema se presenten al usuario con el mismo criterio.
 */
public abstract class PanelBase extends JPanel {

    private static final long serialVersionUID = 1L;

    protected final FabricaControladores controladores;
    protected final Usuario usuario;
    private final JLabel estado = new JLabel(" ");

    protected PanelBase(FabricaControladores controladores, Usuario usuario) {
        super(new BorderLayout(10, 10));
        this.controladores = controladores;
        this.usuario = usuario;
        setBorder(Estilo.margen(12));
        estado.setName("estado");
        estado.setForeground(Estilo.VERDE);
    }

    /** Título de la pestaña. */
    public abstract String getTitulo();

    /** Vuelve a consultar la base y actualiza la pantalla. */
    protected abstract void cargarDatos() throws InventarioException;

    /** Recarga la pantalla mostrando el error, si lo hubiera, sin cerrar la aplicación. */
    public final void refrescar() {
        try {
            cargarDatos();
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }

    /** Etiqueta inferior donde cada pantalla confirma las operaciones realizadas. */
    protected JLabel getEtiquetaEstado() {
        return estado;
    }

    protected void informar(String mensaje) {
        estado.setForeground(Estilo.VERDE);
        estado.setText(mensaje);
    }

    /**
     * Muestra la excepción con un título acorde a su tipo: los datos inválidos, duplicados o en uso son
     * advertencias que el usuario puede resolver; los demás errores de base de datos indican un problema
     * técnico (por ejemplo, MySQL detenido). Las subclases se evalúan antes que su clase padre.
     */
    protected void mostrarError(InventarioException e) {
        String titulo;
        int tipo;
        if (e instanceof DatoInvalidoException) {
            titulo = "Dato inválido";
            tipo = JOptionPane.WARNING_MESSAGE;
        } else if (e instanceof DatoDuplicadoException) {
            titulo = "Dato duplicado";
            tipo = JOptionPane.WARNING_MESSAGE;
        } else if (e instanceof RegistroEnUsoException) {
            titulo = "Operación no permitida";
            tipo = JOptionPane.WARNING_MESSAGE;
        } else if (e instanceof PersistenciaException) {
            titulo = "Error de base de datos";
            tipo = JOptionPane.ERROR_MESSAGE;
        } else {
            titulo = "Operación no permitida";
            tipo = JOptionPane.WARNING_MESSAGE;
        }
        estado.setForeground(Estilo.GRIS_TEXTO);
        estado.setText(" ");
        JOptionPane.showMessageDialog(this, e.getMessage(), titulo, tipo);
    }

    protected boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Convierte el texto de un campo en entero; si no es un número, lanza DatoInvalidoException. */
    protected static int leerEntero(String texto, String campo) throws DatoInvalidoException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("El campo \"" + campo + "\" debe ser un número entero.");
        }
    }

    /** Convierte el texto de un campo en importe (acepta coma o punto decimal). */
    protected static double leerImporte(String texto, String campo) throws DatoInvalidoException {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("El campo \"" + campo + "\" debe ser un importe válido.");
        }
    }
}
