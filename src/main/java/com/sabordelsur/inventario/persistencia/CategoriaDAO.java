package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;
import com.sabordelsur.inventario.modelo.Categoria;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Acceso a datos (JDBC) para la tabla categoria (UC-02). */
public class CategoriaDAO extends DAOBase<Categoria> implements CrudDAO<Categoria> {

    @Override
    protected String getNombreEntidad() {
        return "categoría";
    }

    @Override
    protected Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id_categoria"), rs.getString("nombre"));
    }

    @Override
    public List<Categoria> listarTodos() throws PersistenciaException {
        return consultarLista("SELECT id_categoria, nombre FROM categoria ORDER BY nombre");
    }

    @Override
    public Categoria buscarPorId(int id) throws InventarioException {
        Categoria categoria = consultarUno("SELECT id_categoria, nombre FROM categoria WHERE id_categoria = ?", id);
        if (categoria == null) {
            throw new InventarioException("No existe ninguna categoría con el ID " + id + ".");
        }
        return categoria;
    }

    @Override
    public int insertar(Categoria categoria) throws InventarioException {
        categoria.validar();
        int id = ejecutarEnTransaccion("dar de alta la categoría", (Connection con) ->
                insertarYObtenerId(con, "INSERT INTO categoria (nombre) VALUES (?)", categoria.getNombre().trim()));
        categoria.setId(id);
        return id;
    }

    @Override
    public void actualizar(Categoria categoria) throws InventarioException {
        categoria.validar();
        ejecutarActualizacion("modificar la categoría",
                "UPDATE categoria SET nombre = ? WHERE id_categoria = ?", categoria.getNombre().trim(), categoria.getId());
    }

    @Override
    public void eliminar(int id) throws InventarioException {
        ejecutarActualizacion("eliminar la categoría", "DELETE FROM categoria WHERE id_categoria = ?", id);
    }
}
