package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.CredencialesInvalidasException;
import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Rol;
import com.sabordelsur.inventario.modelo.Usuario;
import com.sabordelsur.inventario.seguridad.Hash;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Acceso a datos (JDBC) para la tabla usuario y su catálogo de roles (UC-01, UC-06). No implementa
 * CrudDAO porque el alta de un usuario necesita la contraseña, que no forma parte del objeto Usuario:
 * la clase solo conserva su hash y no lo expone.
 */
public class UsuarioDAO extends DAOBase<Usuario> {

    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.nombre_usuario, u.contrasena AS hash_contrasena, r.nombre AS rol " +
            "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol ";
    private static final String ID_ROL = "(SELECT id_rol FROM rol WHERE nombre = ?)";

    @Override
    protected String getNombreEntidad() {
        return "usuario";
    }

    @Override
    protected Usuario mapear(ResultSet rs) throws SQLException {
        return Usuario.crear(
                Rol.valueOf(rs.getString("rol")),
                rs.getInt("id_usuario"),
                rs.getString("nombre_usuario"),
                rs.getString("hash_contrasena"));
    }

    /**
     * UC-01: busca al usuario por nombre y valida la contraseña. Devuelve la subclase de Usuario que
     * corresponde a su rol (Administrador u Operador).
     */
    public Usuario autenticar(String nombreUsuario, String contrasena)
            throws CredencialesInvalidasException, PersistenciaException {
        Usuario usuario = consultarUno(SELECT_BASE + "WHERE u.nombre_usuario = ?", nombreUsuario);
        if (usuario == null || !usuario.iniciarSesion(contrasena)) {
            throw new CredencialesInvalidasException();
        }
        return usuario;
    }

    /** UC-06: lista los usuarios registrados con su rol. */
    public List<Usuario> listarTodos() throws PersistenciaException {
        return consultarLista(SELECT_BASE + "ORDER BY r.nombre, u.nombre_usuario");
    }

    /** UC-06 (alta): guarda el usuario con el hash SHA-256 de la contraseña, nunca la contraseña en claro. */
    public int insertar(String nombreUsuario, String contrasena, Rol rol) throws InventarioException {
        return ejecutarEnTransaccion("dar de alta el usuario", con ->
                insertarYObtenerId(con,
                        "INSERT INTO usuario (nombre_usuario, contrasena, id_rol) VALUES (?, ?, " + ID_ROL + ")",
                        nombreUsuario, Hash.sha256(contrasena), rol.name()));
    }

    /** UC-06: asigna un rol distinto al usuario. */
    public void cambiarRol(int idUsuario, Rol rol) throws InventarioException {
        ejecutarActualizacion("cambiar el rol", "UPDATE usuario SET id_rol = " + ID_ROL + " WHERE id_usuario = ?",
                rol.name(), idUsuario);
    }

    /** UC-06: reemplaza la contraseña del usuario por una nueva (se guarda su hash). */
    public void restablecerContrasena(int idUsuario, String nuevaContrasena) throws InventarioException {
        ejecutarActualizacion("restablecer la contraseña", "UPDATE usuario SET contrasena = ? WHERE id_usuario = ?",
                Hash.sha256(nuevaContrasena), idUsuario);
    }

    /**
     * UC-06 (baja): si el usuario ya registró movimientos, la clave foránea impide borrarlo y se informa
     * con RegistroEnUsoException, para no perder la trazabilidad de quién movió cada unidad.
     */
    public void eliminar(int idUsuario) throws InventarioException {
        ejecutarActualizacion("eliminar el usuario", "DELETE FROM usuario WHERE id_usuario = ?", idUsuario);
    }
}
