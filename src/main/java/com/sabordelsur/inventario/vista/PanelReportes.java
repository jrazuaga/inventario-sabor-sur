package com.sabordelsur.inventario.vista;

import com.sabordelsur.inventario.controlador.ControladorReportes;
import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Usuario;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.nio.file.Path;
import java.util.List;

/** Exportación de reportes a CSV y consulta de la bitácora guardada en archivo. */
public class PanelReportes extends PanelBase {

    private static final long serialVersionUID = 1L;
    private static final int LINEAS_BITACORA = 200;

    private final ControladorReportes controlador;
    private final JTextArea areaBitacora = new JTextArea();

    public PanelReportes(FabricaControladores controladores, Usuario usuario) {
        super(controladores, usuario);
        this.controlador = controladores.reportes();

        JButton stock = Estilo.boton("Exportar stock (CSV)", "rep.stock");
        JButton movimientos = Estilo.boton("Exportar movimientos (CSV)", "rep.movimientos");
        JButton actualizar = Estilo.boton("Actualizar bitácora", "rep.actualizar");
        stock.addActionListener(e -> exportar(true));
        movimientos.addActionListener(e -> exportar(false));
        actualizar.addActionListener(e -> refrescar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        botones.add(stock);
        botones.add(movimientos);
        botones.add(actualizar);

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.setOpaque(false);
        norte.add(Estilo.titulo("Reportes y bitácora"), BorderLayout.NORTH);
        norte.add(botones, BorderLayout.CENTER);
        norte.add(Estilo.ayuda("Los reportes se guardan en la carpeta \"" + ControladorReportes.CARPETA_POR_DEFECTO
                + "\" y se abren con Excel o LibreOffice. La bitácora registra cada operación en "
                + controlador.getArchivoBitacora() + "."), BorderLayout.SOUTH);

        areaBitacora.setName("rep.bitacora");
        areaBitacora.setEditable(false);
        areaBitacora.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane desplazamiento = new JScrollPane(areaBitacora);
        desplazamiento.setBorder(Estilo.seccion("Últimas operaciones registradas (la más reciente arriba)"));

        add(norte, BorderLayout.NORTH);
        add(desplazamiento, BorderLayout.CENTER);
        add(getEtiquetaEstado(), BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Reportes";
    }

    @Override
    protected void cargarDatos() throws InventarioException {
        List<String> lineas = controlador.leerBitacora(LINEAS_BITACORA);
        areaBitacora.setText(String.join("\n", lineas));
        areaBitacora.setCaretPosition(0);
    }

    private void exportar(boolean esStock) {
        try {
            Path archivo = esStock ? controlador.exportarStock(usuario) : controlador.exportarMovimientos(usuario);
            cargarDatos();
            informar("Archivo generado: " + archivo.toAbsolutePath());
        } catch (InventarioException e) {
            mostrarError(e);
        }
    }
}
