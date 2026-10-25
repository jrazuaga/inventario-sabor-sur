package com.sabordelsur.inventario;

import com.sabordelsur.inventario.controlador.FabricaControladores;
import com.sabordelsur.inventario.vista.AplicacionGUI;
import com.sabordelsur.inventario.vista.AppConsola;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;

/**
 * Punto de entrada del Sistema de Gestión de Inventario y Reposición. Por defecto abre la interfaz
 * gráfica (Swing); con el argumento --consola, o en un equipo sin entorno gráfico, usa el menú de consola.
 *
 * Requiere una instancia de MySQL con la base creada a partir de sabor_sur_db.sql y los datos de
 * conexión en config/sabor_sur.properties (o en las variables de entorno SABOR_SUR_DB_*).
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        boolean modoConsola = Arrays.asList(args).contains("--consola") || GraphicsEnvironment.isHeadless();
        if (modoConsola) {
            new AppConsola(new FabricaControladores()).ejecutar();
        } else {
            AplicacionGUI.iniciar();
        }
    }
}
