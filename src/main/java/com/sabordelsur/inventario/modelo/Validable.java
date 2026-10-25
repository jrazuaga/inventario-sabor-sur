package com.sabordelsur.inventario.modelo;

import com.sabordelsur.inventario.excepciones.InventarioException;

/**
 * Contrato común de las entidades que verifican sus propias reglas antes de guardarse. Los
 * controladores y los DAO llaman a validar() sin necesidad de conocer la clase concreta.
 */
public interface Validable {

    void validar() throws InventarioException;
}
