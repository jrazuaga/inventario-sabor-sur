package com.sabordelsur.inventario.modelo;

/** Representa a cada proveedor con el que la distribuidora puede adquirir productos (UC-04, UC-05). */
public class Proveedor {

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

    @Override
    public String toString() {
        return nombre;
    }
}
