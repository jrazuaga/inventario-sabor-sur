package com.sabordelsur.inventario.controlador;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.persistencia.CrudDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.function.ObjIntConsumer;

/**
 * Implementación de CrudDAO que guarda los objetos en un ArrayList. Permite probar los controladores
 * sin MySQL, gracias a que dependen de la interfaz y no de la clase JDBC.
 */
class RepositorioEnMemoria<T> implements CrudDAO<T> {

    private final List<T> datos = new ArrayList<>();
    private final ToIntFunction<T> obtenerId;
    private final ObjIntConsumer<T> asignarId;
    private int ultimoId = 0;
    int actualizaciones = 0;

    RepositorioEnMemoria(ToIntFunction<T> obtenerId, ObjIntConsumer<T> asignarId) {
        this.obtenerId = obtenerId;
        this.asignarId = asignarId;
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(datos);
    }

    @Override
    public T buscarPorId(int id) throws InventarioException {
        for (T dato : datos) {
            if (obtenerId.applyAsInt(dato) == id) {
                return dato;
            }
        }
        throw new InventarioException("No existe el ID " + id);
    }

    @Override
    public int insertar(T entidad) {
        asignarId.accept(entidad, ++ultimoId);
        datos.add(entidad);
        return ultimoId;
    }

    @Override
    public void actualizar(T entidad) {
        actualizaciones++;
    }

    @Override
    public void eliminar(int id) {
        datos.removeIf(d -> obtenerId.applyAsInt(d) == id);
    }
}
