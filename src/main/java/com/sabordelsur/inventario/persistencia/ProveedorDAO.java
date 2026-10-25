package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Proveedor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Acceso a datos (JDBC) para la tabla proveedor (UC-03). */
public class ProveedorDAO extends DAOBase<Proveedor> implements CrudDAO<Proveedor> {

    private static final String SELECT_BASE = "SELECT id_proveedor, nombre, contacto FROM proveedor ";

    @Override
    protected String getNombreEntidad() {
        return "proveedor";
    }

    @Override
    protected Proveedor mapear(ResultSet rs) throws SQLException {
        return new Proveedor(rs.getInt("id_proveedor"), rs.getString("nombre"), rs.getString("contacto"));
    }

    @Override
    public List<Proveedor> listarTodos() throws PersistenciaException {
        return consultarLista(SELECT_BASE + "ORDER BY nombre");
    }

    @Override
    public Proveedor buscarPorId(int id) throws InventarioException {
        Proveedor proveedor = consultarUno(SELECT_BASE + "WHERE id_proveedor = ?", id);
        if (proveedor == null) {
            throw new InventarioException("No existe ningún proveedor con el ID " + id + ".");
        }
        return proveedor;
    }

    @Override
    public int insertar(Proveedor proveedor) throws InventarioException {
        proveedor.validar();
        int id = ejecutarEnTransaccion("dar de alta el proveedor", con ->
                insertarYObtenerId(con, "INSERT INTO proveedor (nombre, contacto) VALUES (?, ?)",
                        proveedor.getNombre().trim(), vacioComoNulo(proveedor.getContacto())));
        proveedor.setId(id);
        return id;
    }

    @Override
    public void actualizar(Proveedor proveedor) throws InventarioException {
        proveedor.validar();
        ejecutarActualizacion("modificar el proveedor",
                "UPDATE proveedor SET nombre = ?, contacto = ? WHERE id_proveedor = ?",
                proveedor.getNombre().trim(), vacioComoNulo(proveedor.getContacto()), proveedor.getId());
    }

    /** UC-03 (baja): elimina primero las ofertas del proveedor (UC-04) y después el proveedor, en una transacción. */
    @Override
    public void eliminar(int id) throws InventarioException {
        ejecutarEnTransaccion("eliminar el proveedor", con -> {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM producto_proveedor WHERE id_proveedor = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM proveedor WHERE id_proveedor = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            return null;
        });
    }

    private static String vacioComoNulo(String texto) {
        return (texto == null || texto.trim().isEmpty()) ? null : texto.trim();
    }
}
