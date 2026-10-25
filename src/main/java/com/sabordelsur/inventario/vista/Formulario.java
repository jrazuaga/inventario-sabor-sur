package com.sabordelsur.inventario.vista;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Panel de formulario de dos columnas (etiqueta y campo) armado con GridBagLayout. */
public class Formulario extends JPanel {

    private static final long serialVersionUID = 1L;

    private int fila = 0;

    public Formulario() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    public Formulario agregar(String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = fila;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(4, 0, 4, 8);
        add(new JLabel(etiqueta), c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = fila++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 4, 0);
        add(campo, c);
        return this;
    }

    /** Agrega un componente que ocupa las dos columnas (botonera, mensajes). */
    public Formulario agregarAncho(JComponent componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = fila++;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 4, 0);
        add(componente, c);
        return this;
    }

    /** Empuja el contenido hacia arriba ocupando el espacio sobrante. */
    public Formulario cerrar() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = fila++;
        c.weighty = 1;
        add(new JLabel(), c);
        return this;
    }
}
