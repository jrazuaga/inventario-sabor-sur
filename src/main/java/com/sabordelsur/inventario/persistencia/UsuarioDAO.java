package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.CredencialesInvalidasException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Rol;
import com.sabordelsur.inventario.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Acceso a datos (JDBC) para la tabla usuario y su catálogo de roles. */
public class UsuarioDAO {

    /**
     * UC-01: busca al usuario por nombre y valida la contraseña. Devuelve la subclase de Usuario que
     * corresponde a su rol (Administrador u Operador).
     */
    public Usuario autenticar(String nombreUsuario, String contrasena)
            throws CredencialesInvalidasException, PersistenciaException {
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.contrasena AS hash_contrasena, r.nombre AS rol " +
                     "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol " +
                     "WHERE u.nombre_usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new CredencialesInvalidasException();
                }
                Usuario usuario = Usuario.crear(
                        Rol.valueOf(rs.getString("rol")),
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("hash_contrasena"));
                if (!usuario.iniciarSesion(contrasena)) {
                    throw new CredencialesInvalidasException();
                }
                return usuario;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al validar las credenciales.", e);
        }
    }
}
