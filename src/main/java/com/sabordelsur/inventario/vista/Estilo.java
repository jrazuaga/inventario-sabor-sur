package com.sabordelsur.inventario.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Font;

/** Colores, tipografías y componentes con formato común para todas las pantallas. */
public final class Estilo {

    public static final Color VERDE = new Color(0x1F, 0x6F, 0x4B);
    public static final Color VERDE_CLARO = new Color(0xE8, 0xF3, 0xEC);
    public static final Color ROJO_SUAVE = new Color(0xF8, 0xD7, 0xDA);
    public static final Color AMBAR_SUAVE = new Color(0xFF, 0xF3, 0xCD);
    public static final Color GRIS_TEXTO = new Color(0x55, 0x55, 0x55);

    public static final Font TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font SUBTITULO = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    private Estilo() {
    }

    /** Activa el aspecto Nimbus (incluido en Java) con el verde institucional; si no está, se usa el aspecto por defecto. */
    public static void aplicarAspecto() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.put("nimbusBase", new Color(0x2E, 0x5E, 0x48));
                    UIManager.put("nimbusSelectionBackground", VERDE);
                    UIManager.put("nimbusFocus", new Color(0x7F, 0xB8, 0x9A));
                    UIManager.put("control", new Color(0xF4, 0xF6, 0xF5));
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Aviso: no se pudo aplicar el aspecto Nimbus (" + e.getMessage() + ").");
        }
        UIManager.put("defaultFont", NORMAL);
    }

    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(TITULO);
        etiqueta.setForeground(VERDE);
        return etiqueta;
    }

    public static JLabel ayuda(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(GRIS_TEXTO);
        return etiqueta;
    }

    /** Botón con nombre interno (setName) para poder ubicarlo en las pruebas de la interfaz. */
    public static JButton boton(String texto, String nombre) {
        JButton boton = new JButton(texto);
        boton.setName(nombre);
        boton.setFocusable(false);
        return boton;
    }

    public static Border margen(int px) {
        return BorderFactory.createEmptyBorder(px, px, px, px);
    }

    public static Border seccion(String titulo) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xC9, 0xD6, 0xCF)), titulo),
                margen(6));
    }

    public static void formatearTabla(JTable tabla) {
        tabla.setRowHeight(24);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(SUBTITULO);
        tabla.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object valor) {
                setHorizontalAlignment(RIGHT);
                setText(valor == null ? "" : String.format("%,.2f", (Double) valor));
            }
        });
    }

    /** Texto de ayuda que se ajusta en varias líneas dentro del ancho indicado. */
    public static JLabel ayudaMultilinea(String texto, int anchoPx) {
        return ayuda("<html><body style='width:" + anchoPx + "px'>" + texto + "</body></html>");
    }

    /** Devuelve "1 producto" o "3 productos" según la cantidad. */
    public static String plural(int cantidad, String singular, String plural) {
        return cantidad + " " + (cantidad == 1 ? singular : plural);
    }
}
