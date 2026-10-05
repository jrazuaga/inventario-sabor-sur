package com.sabordelsur.inventario.modelo;

import java.util.List;

/**
 * Representa a cada persona que accede al sistema. Es una clase abstracta: no existe un "usuario
 * genérico", sino un Administrador o un Operador, y cada uno define qué opciones del menú puede usar.
 * Los atributos son privados (encapsulamiento) y la contraseña no tiene getter: solo se compara
 * dentro de la propia clase.
 */
public abstract class Usuario {

    private final int id;
    private final String nombreUsuario;
    private final String contrasena;

    protected Usuario(int id, String nombreUsuario, String contrasena) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    /** Método de fábrica: devuelve la subclase que corresponde al rol almacenado en la base. */
    public static Usuario crear(Rol rol, int id, String nombreUsuario, String contrasena) {
        switch (rol) {
            case ADMINISTRADOR:
                return new Administrador(id, nombreUsuario, contrasena);
            case OPERADOR:
                return new Operador(id, nombreUsuario, contrasena);
            default:
                throw new IllegalArgumentException("Rol no soportado: " + rol);
        }
    }

    /**
     * UC-01: valida la contraseña ingresada. En este prototipo la comparación es directa; en un
     * entorno productivo se almacenaría un hash (por ejemplo BCrypt) y se compararía el hash.
     */
    public boolean iniciarSesion(String contrasenaIngresada) {
        return contrasena != null && contrasena.equals(contrasenaIngresada);
    }

    /** Rol del usuario. Lo define cada subclase. */
    public abstract Rol getRol();

    /** Opciones del menú que este tipo de usuario puede ejecutar. Lo define cada subclase. */
    public abstract List<OpcionMenu> getOpcionesPermitidas();

    public boolean puedeAcceder(OpcionMenu opcion) {
        return getOpcionesPermitidas().contains(opcion);
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + getRol() + ")";
    }
}
