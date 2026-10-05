package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.seguridad.Hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Representa a cada persona que accede al sistema. Es una clase abstracta: no existe un "usuario
 * genérico", sino un Administrador o un Operador, y cada uno define qué opciones del menú puede usar.
 * Los atributos son privados (encapsulamiento). De la contraseña solo se guarda su hash SHA-256 y no
 * tiene getter: únicamente se compara dentro de la propia clase.
 */
public abstract class Usuario {

    private final int id;
    private final String nombreUsuario;
    private final String hashContrasena;

    protected Usuario(int id, String nombreUsuario, String hashContrasena) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.hashContrasena = hashContrasena;
    }

    /** Método de fábrica: devuelve la subclase que corresponde al rol almacenado en la base. */
    public static Usuario crear(Rol rol, int id, String nombreUsuario, String hashContrasena) {
        switch (rol) {
            case ADMINISTRADOR:
                return new Administrador(id, nombreUsuario, hashContrasena);
            case OPERADOR:
                return new Operador(id, nombreUsuario, hashContrasena);
            default:
                throw new IllegalArgumentException("Rol no soportado: " + rol);
        }
    }

    /**
     * UC-01: valida la contraseña ingresada. Se calcula su SHA-256 y se lo compara con el hash
     * almacenado. En un entorno productivo conviene un algoritmo con sal y costo ajustable (BCrypt o
     * Argon2); aquí se usa SHA-256 para no agregar dependencias externas.
     */
    public boolean iniciarSesion(String contrasenaIngresada) {
        if (hashContrasena == null || contrasenaIngresada == null) {
            return false;
        }
        byte[] guardado = hashContrasena.getBytes(StandardCharsets.UTF_8);
        byte[] ingresado = Hash.sha256(contrasenaIngresada).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(guardado, ingresado);
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
