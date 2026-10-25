package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.excepciones.DatoInvalidoException;

/** Representa a cada proveedor con el que la distribuidora puede adquirir productos (UC-03, UC-04, UC-05). */
public class Proveedor implements Validable {

    private int id;
    private String nombre;
    private String contacto;

    public Proveedor() {
    }

    public Proveedor(int id, String nombre, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
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

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /** UC-03: el nombre es obligatorio; el contacto es opcional pero tiene un largo máximo. */
    @Override
    public void validar() throws DatoInvalidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre del proveedor es obligatorio.");
        }
        if (nombre.trim().length() > 100) {
            throw new DatoInvalidoException("El nombre del proveedor admite hasta 100 caracteres.");
        }
        if (contacto != null && contacto.trim().length() > 150) {
            throw new DatoInvalidoException("El contacto admite hasta 150 caracteres.");
        }
    }

    @Override
    public String toString() {
        return nombre;
    }
}
