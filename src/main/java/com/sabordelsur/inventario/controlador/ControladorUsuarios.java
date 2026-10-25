package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.archivos.Bitacora;
import com.sabordelsur.inventario.excepciones.DatoInvalidoException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Rol;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.persistencia.UsuarioDAO;

import java.util.List;

/** UC-06: gestión de usuarios y roles. Solo la usa el administrador. */
public class ControladorUsuarios {

    public static final int LARGO_MINIMO_CONTRASENA = 8;
    private static final String PATRON_NOMBRE = "[A-Za-z0-9._-]{3,50}";

    private final UsuarioDAO usuarioDAO;
    private final Bitacora bitacora;

    public ControladorUsuarios(UsuarioDAO usuarioDAO, Bitacora bitacora) {
        this.usuarioDAO = usuarioDAO;
        this.bitacora = bitacora;
    }

    public List<Usuario> listar() throws PersistenciaException {
        return usuarioDAO.listarTodos();
    }

    public void crear(String nombreUsuario, String contrasena, Rol rol, Usuario actor) throws InventarioException {
        validarNombre(nombreUsuario);
        validarContrasena(contrasena);
        if (rol == null) {
            throw new DatoInvalidoException("Debe seleccionar un rol.");
        }
        usuarioDAO.insertar(nombreUsuario.trim(), contrasena, rol);
        bitacora.registrar(actor.getNombreUsuario(), "Alta de usuario: " + nombreUsuario.trim() + " (" + rol + ")");
    }

    public void cambiarRol(Usuario usuario, Rol nuevoRol, Usuario actor) throws InventarioException {
        if (usuario.getId() == actor.getId()) {
            throw new DatoInvalidoException("No puede cambiar su propio rol: otro administrador debe hacerlo.");
        }
        usuarioDAO.cambiarRol(usuario.getId(), nuevoRol);
        bitacora.registrar(actor.getNombreUsuario(), "Cambio de rol: " + usuario.getNombreUsuario() + " -> " + nuevoRol);
    }

    public void restablecerContrasena(Usuario usuario, String nuevaContrasena, Usuario actor) throws InventarioException {
        validarContrasena(nuevaContrasena);
        usuarioDAO.restablecerContrasena(usuario.getId(), nuevaContrasena);
        bitacora.registrar(actor.getNombreUsuario(), "Contraseña restablecida para " + usuario.getNombreUsuario());
    }

    public void eliminar(Usuario usuario, Usuario actor) throws InventarioException {
        if (usuario.getId() == actor.getId()) {
            throw new DatoInvalidoException("No puede eliminar el usuario con el que inició la sesión.");
        }
        usuarioDAO.eliminar(usuario.getId());
        bitacora.registrar(actor.getNombreUsuario(), "Baja de usuario: " + usuario.getNombreUsuario());
    }

    static void validarNombre(String nombreUsuario) throws DatoInvalidoException {
        if (nombreUsuario == null || !nombreUsuario.trim().matches(PATRON_NOMBRE)) {
            throw new DatoInvalidoException("El nombre de usuario debe tener entre 3 y 50 caracteres, "
                    + "sin espacios (letras, números, punto, guion o guion bajo).");
        }
    }

    static void validarContrasena(String contrasena) throws DatoInvalidoException {
        if (contrasena == null || contrasena.length() < LARGO_MINIMO_CONTRASENA) {
            throw new DatoInvalidoException("La contraseña debe tener al menos " + LARGO_MINIMO_CONTRASENA + " caracteres.");
        }
    }
}
