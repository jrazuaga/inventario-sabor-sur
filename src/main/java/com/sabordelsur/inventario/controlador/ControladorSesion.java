package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.excepciones.CredencialesInvalidasException;
import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.UsuarioDAO;

/** UC-01: inicio y cierre de sesión. Lo usan tanto la interfaz gráfica como el modo consola. */
public class ControladorSesion {

    public static final int MAX_INTENTOS = 3;

    private final UsuarioDAO usuarioDAO;
    private final Bitacora bitacora;

    public ControladorSesion(UsuarioDAO usuarioDAO, Bitacora bitacora) {
        this.usuarioDAO = usuarioDAO;
        this.bitacora = bitacora;
    }

    public Usuario iniciarSesion(String nombreUsuario, String contrasena) throws InventarioException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() || contrasena == null || contrasena.isEmpty()) {
            throw new DatoInvalidoException("Ingrese el usuario y la contraseña.");
        }
        try {
            Usuario usuario = usuarioDAO.autenticar(nombreUsuario.trim(), contrasena);
            bitacora.registrar(usuario.getNombreUsuario(), "Inicio de sesión (" + usuario.getRol() + ")");
            return usuario;
        } catch (CredencialesInvalidasException e) {
            bitacora.registrar(nombreUsuario.trim(), "Intento de inicio de sesión fallido");
            throw e;
        }
    }

    public void cerrarSesion(Usuario usuario) {
        bitacora.registrar(usuario.getNombreUsuario(), "Cierre de sesión");
    }
}
