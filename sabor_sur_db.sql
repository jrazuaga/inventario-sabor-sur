-- ============================================================
-- Sistema de Gestión de Inventario y Reposición
-- Distribuidora Sabor Sur S.R.L.
-- Script de base de datos MySQL — Trabajo Práctico 3 (versión normalizada)
-- Alumno: Julián Azuaga
--
-- Cambios respecto del TP2:
--   * Los valores fijos (rol, tipo de movimiento, estado de pedido) pasan de columnas ENUM a tablas
--     catálogo referenciadas por clave foránea (normalización).
--   * producto_proveedor incorpora plazo_entrega_dias, requerido por RF-03 / UC-05.
-- ============================================================

DROP DATABASE IF EXISTS sabor_sur_db;
CREATE DATABASE sabor_sur_db CHARACTER SET utf8mb4;
USE sabor_sur_db;

-- ------------------------------------------------------------
-- 1. CREACIÓN DE TABLAS
-- ------------------------------------------------------------

-- Catálogos (valores fijos del dominio)
CREATE TABLE rol (
    id_rol INT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE tipo_movimiento (
    id_tipo INT PRIMARY KEY,
    nombre  VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE estado_pedido (
    id_estado INT PRIMARY KEY,
    nombre    VARCHAR(20) NOT NULL UNIQUE
);

-- Tablas del negocio
CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL
);

CREATE TABLE producto (
    id_producto   INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    id_categoria  INT NOT NULL,
    stock_actual  INT NOT NULL DEFAULT 0,
    stock_minimo  INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE proveedor (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    contacto     VARCHAR(150)
);

CREATE TABLE producto_proveedor (
    id_producto        INT NOT NULL,
    id_proveedor       INT NOT NULL,
    precio             DECIMAL(10,2) NOT NULL,
    plazo_entrega_dias INT NOT NULL,
    disponible         BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_producto, id_proveedor),
    CONSTRAINT fk_pp_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT fk_pp_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
);

CREATE TABLE usuario (
    id_usuario     INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena     VARCHAR(255) NOT NULL,
    id_rol         INT NOT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE movimiento_stock (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_producto   INT NOT NULL,
    id_usuario    INT NOT NULL,
    fecha         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_tipo       INT NOT NULL,
    cantidad      INT NOT NULL,
    CONSTRAINT fk_mov_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT fk_mov_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_mov_tipo
        FOREIGN KEY (id_tipo) REFERENCES tipo_movimiento(id_tipo)
);

CREATE TABLE pedido_reposicion (
    id_pedido         INT AUTO_INCREMENT PRIMARY KEY,
    id_producto       INT NOT NULL,
    fecha_generacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_estado         INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_pedido_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT fk_pedido_estado
        FOREIGN KEY (id_estado) REFERENCES estado_pedido(id_estado)
);

-- ------------------------------------------------------------
-- 2. INSERCIÓN DE REGISTROS
-- ------------------------------------------------------------

INSERT INTO rol (id_rol, nombre) VALUES
    (1, 'ADMINISTRADOR'),
    (2, 'OPERADOR');

INSERT INTO tipo_movimiento (id_tipo, nombre) VALUES
    (1, 'ENTRADA'),
    (2, 'SALIDA');

INSERT INTO estado_pedido (id_estado, nombre) VALUES
    (1, 'PENDIENTE'),
    (2, 'RESUELTO');

INSERT INTO categoria (nombre) VALUES
    ('Aceites'),
    ('Harinas'),
    ('Descartables');

INSERT INTO producto (nombre, id_categoria, stock_actual, stock_minimo) VALUES
    ('Aceite de girasol 5L', 1, 12, 10),
    ('Aceite de oliva 1L',   1,  4,  5),
    ('Harina 0000 x25kg',    2, 20, 15),
    ('Harina integral x25kg',2,  8, 10),
    ('Vaso descartable x50', 3, 30, 20);

INSERT INTO proveedor (nombre, contacto) VALUES
    ('Aceitera del Sur S.A.',       'ventas@aceiteradelsur.com'),
    ('Molinos Santa Fe',            'pedidos@molinossantafe.com'),
    ('Descartables Rosario S.R.L.', 'contacto@descartablesrosario.com'),
    ('Importadora Mediterránea',    'compras@importadoramediterranea.com');

INSERT INTO producto_proveedor (id_producto, id_proveedor, precio, plazo_entrega_dias, disponible) VALUES
    (1, 1, 4200.00, 3, TRUE),
    (1, 4, 4050.00, 5, TRUE),
    (2, 1, 6800.00, 3, TRUE),
    (2, 4, 6500.00, 7, TRUE),
    (3, 2, 9500.00, 2, TRUE),
    (4, 2, 9900.00, 2, TRUE),
    (5, 3, 3100.00, 1, TRUE);

INSERT INTO usuario (nombre_usuario, contrasena, id_rol) VALUES
    ('propietario',  'hash_propietario', 1),
    ('operario1',    'hash_operario1',   2),
    ('operario2',    'hash_operario2',   2);

INSERT INTO movimiento_stock (id_producto, id_usuario, fecha, id_tipo, cantidad) VALUES
    (1, 2, '2026-09-01 08:15:00', 1, 20),
    (1, 2, '2026-09-01 15:40:00', 2,  8),
    (2, 3, '2026-09-02 09:05:00', 2,  6),
    (4, 3, '2026-09-02 11:30:00', 2,  7),
    (3, 2, '2026-09-03 10:20:00', 1, 20);

INSERT INTO pedido_reposicion (id_producto, fecha_generacion, id_estado) VALUES
    (2, '2026-09-02 09:05:00', 1),
    (4, '2026-09-02 11:30:00', 1);

-- ------------------------------------------------------------
-- 3. CONSULTA DE REGISTROS
-- ------------------------------------------------------------

-- 3.1 Consultar stock actual de todos los productos, agrupado por categoría (UC-11)
SELECT c.nombre AS categoria, p.nombre AS producto, p.stock_actual, p.stock_minimo
FROM producto p
JOIN categoria c ON c.id_categoria = p.id_categoria
ORDER BY c.nombre, p.nombre;

-- 3.2 Comparar condiciones de proveedores para un producto puntual (UC-05)
SELECT p.nombre AS producto, pv.nombre AS proveedor, pp.precio, pp.plazo_entrega_dias, pp.disponible
FROM producto_proveedor pp
JOIN producto  p  ON p.id_producto  = pp.id_producto
JOIN proveedor pv ON pv.id_proveedor = pp.id_proveedor
WHERE p.nombre = 'Aceite de oliva 1L'
ORDER BY pp.precio ASC;

-- 3.3 Listar los pedidos de reposición pendientes, con el producto asociado (UC-07)
SELECT pr.id_pedido, p.nombre AS producto, p.stock_actual, p.stock_minimo, pr.fecha_generacion
FROM pedido_reposicion pr
JOIN producto p        ON p.id_producto = pr.id_producto
JOIN estado_pedido e   ON e.id_estado   = pr.id_estado
WHERE e.nombre = 'PENDIENTE'
ORDER BY pr.fecha_generacion, pr.id_pedido;

-- 3.4 Historial de movimientos de un producto, con el usuario que los registró (trazabilidad)
SELECT m.fecha, t.nombre AS tipo, m.cantidad, u.nombre_usuario
FROM movimiento_stock m
JOIN tipo_movimiento t ON t.id_tipo    = m.id_tipo
JOIN usuario u         ON u.id_usuario = m.id_usuario
WHERE m.id_producto = 1
ORDER BY m.fecha;

-- 3.5 Productos cuyo stock actual está por debajo del umbral mínimo (verificación de la regla de negocio)
SELECT nombre, stock_actual, stock_minimo
FROM producto
WHERE stock_actual < stock_minimo;

-- ------------------------------------------------------------
-- 4. BORRADO DE REGISTROS
-- ------------------------------------------------------------

-- 4.1 Ingresó mercadería de Aceite de oliva 1L (entrada de 10 unidades): el stock vuelve a superar el mínimo
--     y el pedido de reposición pendiente de ese producto se marca como resuelto (UC-10).
UPDATE producto SET stock_actual = stock_actual + 10 WHERE id_producto = 2;

UPDATE pedido_reposicion
SET id_estado = (SELECT id_estado FROM estado_pedido WHERE nombre = 'RESUELTO')
WHERE id_pedido = 1;

-- 4.2 Borrado de un pedido de reposición ya resuelto, para depurar la tabla de pedidos históricos.
DELETE FROM pedido_reposicion
WHERE id_pedido = 1
  AND id_estado = (SELECT id_estado FROM estado_pedido WHERE nombre = 'RESUELTO');

-- 4.3 Borrado de una relación producto-proveedor cuando el proveedor deja de ofrecer ese producto.
DELETE FROM producto_proveedor
WHERE id_producto = 5 AND id_proveedor = 3;
