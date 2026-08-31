package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.modelo.Rol;
import com.sabordelsur.inventario.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Acceso a datos (JDBC) para la tabla usuario. */
public class UsuarioDAO {

    /** UC-01: busca el usuario por nombre de usuario para validar sus credenciales en la capa de servicio. */
    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT id_usuario, nombre_usuario, contrasena, rol " +
                     "FROM usuario WHERE nombre_usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contrasena"),
                        Rol.valueOf(rs.getString("rol"))
                );
            }
        }
    }
}
