package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.excepciones.DatoInvalidoException;

/** Agrupa productos con características o tratamiento similar (ej.: "Aceites", "Harinas", "Descartables"). */
public class Categoria implements Validable {

    private int id;
    private String nombre;

    public Categoria() {
    }

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void validar() throws DatoInvalidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre de la categoría es obligatorio.");
        }
        if (nombre.trim().length() > 100) {
            throw new DatoInvalidoException("El nombre de la categoría admite hasta 100 caracteres.");
        }
    }

    @Override
    public String toString() {
        return nombre;
    }
}
