package com.sabordelsur.inventario.modelo;

/** Representa a cada persona que accede al sistema (propietario u operario de depósito). */
public class Usuario {

    private int id;
    private String nombreUsuario;
    private String contrasena;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(int id, String nombreUsuario, String contrasena, Rol rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    /**
     * UC-01: valida las credenciales ingresadas contra la contraseña almacenada.
     * En este prototipo la comparación es directa; en un entorno productivo la contraseña
     * se almacena con un algoritmo de hash (ej. BCrypt) y se compara el hash, nunca el texto plano.
     */
    public boolean iniciarSesion(String contrasenaIngresada) {
        return this.contrasena != null && this.contrasena.equals(contrasenaIngresada);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + rol + ")";
    }
}
