# Sistema de Gestión de Inventario y Reposición — Distribuidora Sabor Sur S.R.L.

Prototipo Java correspondiente a la **etapa de implementación** del Trabajo Práctico 2 —
Seminario de Práctica de Informática, Universidad Siglo 21 (alumno: Julián Azuaga).

Implementa, sobre una base de datos MySQL, los casos de uso definidos en el Trabajo Práctico 1
(UC-01, UC-05, UC-07, UC-08, UC-09, UC-10, UC-11), siguiendo el diagrama de clases y el modelo
relacional presentados en el TP2 (secciones 10 y 11).

## Estructura del proyecto

```
inventario-sabor-sur/
├── pom.xml
├── sabor_sur_db.sql                  (script de base de datos — ver raíz de la entrega)
└── src/main/java/com/sabordelsur/inventario/
    ├── Main.java                     (prototipo de consola)
    ├── modelo/                       (clases de diseño: Producto, Categoria, Proveedor,
    │                                  ProductoProveedor, Usuario, MovimientoStock,
    │                                  PedidoReposicion, y los enums Rol/TipoMovimiento/EstadoPedido)
    └── persistencia/                 (capa JDBC: ConexionBD y los DAO de cada tabla)
```

## Requisitos

- Java 11 o superior (JDK).
- Maven 3.8 o superior.
- Un servidor MySQL 8.x accesible localmente.

## Puesta en marcha

1. Crear la base de datos y las tablas ejecutando `sabor_sur_db.sql` (incluido en la raíz de la
   entrega del TP2) contra el servidor MySQL local:

   ```bash
   mysql -u root -p < sabor_sur_db.sql
   ```

2. Editar `src/main/java/com/sabordelsur/inventario/persistencia/ConexionBD.java` con el usuario
   y la contraseña del servidor MySQL local.

3. Compilar y empaquetar con Maven:

   ```bash
   mvn clean package
   ```

4. Ejecutar el prototipo:

   ```bash
   java -cp target/inventario-sabor-sur.jar:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q) com.sabordelsur.inventario.Main
   ```

   (o, más simple, abrir el proyecto en un IDE como IntelliJ IDEA o Eclipse, que resuelve las
   dependencias del `pom.xml` automáticamente y permite ejecutar `Main.java` directamente.)

5. Iniciar sesión con alguno de los usuarios de prueba cargados por el script
   (`propietario` / `operario1` / `operario2`) y recorrer el menú.

## Relación con el resto de la entrega

- El diagrama de clases de diseño del que derivan estas clases está en la sección 10 del TP2.
- El modelo relacional y el diagrama entidad-relación están en la sección 11.
- El script SQL completo (creación de tablas, carga de datos, consultas) está en la sección 12
  y en el archivo `sabor_sur_db.sql`.
- Este código implementa la sección 13 (Etapa de implementación).
