package com.sabordelsur.inventario.persistencia;

import com.sabordelsur.inventario.excepciones.InventarioException;
import com.sabordelsur.inventario.excepciones.PersistenciaException;

import java.util.List;

/**
 * Contrato genérico de acceso a datos (patrón DAO) para las entidades con alta, baja, modificación y
 * consulta. Los controladores dependen de esta interfaz y no de la clase JDBC concreta, lo que permite
 * reemplazar la implementación (por ejemplo, por un repositorio en memoria en las pruebas).
 *
 * @param <T> entidad del modelo que se persiste
 */
public interface CrudDAO<T> {

    List<T> listarTodos() throws PersistenciaException;

    T buscarPorId(int id) throws InventarioException;

    /** Inserta la entidad y devuelve el identificador generado por la base. */
    int insertar(T entidad) throws InventarioException;

    void actualizar(T entidad) throws InventarioException;

    void eliminar(int id) throws InventarioException;
}
