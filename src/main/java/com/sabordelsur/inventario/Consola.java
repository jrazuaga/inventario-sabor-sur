package com.sabordelsur.inventario;

import java.util.Scanner;

/**
 * Lectura de datos por consola con validación. Cada método repite la pregunta hasta recibir un valor
 * válido, de modo que un dato mal tipeado no interrumpa el programa. Si la entrada se cierra
 * (por ejemplo, Ctrl+D), Scanner lanza NoSuchElementException y el programa finaliza ordenadamente.
 */
public class Consola {

    private final Scanner scanner;

    public Consola(Scanner scanner) {
        this.scanner = scanner;
    }

    public String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        return scanner.nextLine().trim();
    }

    public int leerEntero(String etiqueta) {
        while (true) {
            String texto = leerTexto(etiqueta);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("  Valor inválido: ingrese un número entero.");
            }
        }
    }

    /** Lee un entero dentro del rango [minimo, maximo], repitiendo la pregunta hasta que sea válido. */
    public int leerEnteroEnRango(String etiqueta, int minimo, int maximo) {
        while (true) {
            int valor = leerEntero(etiqueta);
            if (valor >= minimo && valor <= maximo) {
                return valor;
            }
            System.out.printf("  Opción fuera de rango: debe estar entre %d y %d.%n", minimo, maximo);
        }
    }
}
