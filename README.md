# Sistema de Gestión de Inventario y Reposición — Distribuidora Sabor Sur S.R.L.

Prototipo Java del **Trabajo Práctico 3** de Seminario de Práctica de Informática, Universidad Siglo 21
(alumno: Julián Azuaga). Continúa el prototipo del TP2 y agrega los pilares de la programación
orientada a objetos, manejo de excepciones, estructuras de datos propias y algoritmos de ordenación y búsqueda.

Trabaja sobre una base MySQL e implementa los casos de uso UC-01, UC-05, UC-07, UC-08, UC-09, UC-10 y UC-11
definidos en el TP1.

## Estructura del proyecto

```
inventario-sabor-sur/
├── pom.xml
├── sabor_sur_db.sql                  (script de la base de datos, versión normalizada)
└── src/
    ├── main/java/com/sabordelsur/inventario/
    │   ├── Main.java                 (menú de consola)
    │   ├── Consola.java              (lectura de datos con validación)
    │   ├── modelo/                   (clases de dominio: Usuario/Administrador/Operador,
    │   │                              MovimientoStock/MovimientoEntrada/MovimientoSalida,
    │   │                              Producto, Categoria, Proveedor, ProductoProveedor, PedidoReposicion, enums)
    │   ├── excepciones/              (InventarioException y sus subclases)
    │   ├── estructuras/              (ColaEnlazada y PilaEnlazada, implementadas con nodos)
    │   ├── algoritmos/               (Ordenamiento: inserción y mezcla; Busqueda: binaria y lineal)
    │   └── persistencia/             (ConexionBD y un DAO por tabla, con JDBC)
    └── test/java/...                 (45 pruebas JUnit 5)
```

## Requisitos

- Java 11 o superior (JDK).
- Maven 3.8 o superior.
- Un servidor MySQL 8.x accesible localmente.

## Puesta en marcha

1. Crear la base de datos y las tablas (el script la elimina y la vuelve a crear):

   ```bash
   mysql -u root -p < sabor_sur_db.sql
   ```

2. Indicar los datos de conexión. Se pueden definir las variables de entorno `SABOR_SUR_DB_USER` y
   `SABOR_SUR_DB_PASSWORD` (y, si hace falta, `SABOR_SUR_DB_URL`), o editar los valores por defecto en
   `src/main/java/com/sabordelsur/inventario/persistencia/ConexionBD.java`.

3. Compilar, ejecutar las pruebas y generar el jar ejecutable:

   ```bash
   mvn clean package
   ```

4. Ejecutar el prototipo:

   ```bash
   java -jar target/inventario-sabor-sur-jar-with-dependencies.jar
   ```

   (También se puede abrir el proyecto en IntelliJ IDEA o Eclipse y ejecutar `Main.java`.)

5. Iniciar sesión con alguno de los usuarios de prueba que carga el script:

   | Usuario       | Contraseña         | Rol            |
   |---------------|--------------------|----------------|
   | `propietario` | `hash_propietario` | Administrador  |
   | `operario1`   | `hash_operario1`   | Operador       |
   | `operario2`   | `hash_operario2`   | Operador       |

   El menú que aparece depende del rol: el Administrador consulta, compara proveedores y supervisa la cola de
   reposición; el Operador consulta y registra movimientos de stock.

## Relación con el resto de la entrega

- El diagrama de clases, los diagramas de secuencia y el modelo relacional están en las secciones 10 y 11 del documento.
- El script SQL completo está en la sección 12 y en el archivo `sabor_sur_db.sql`.
- La explicación de cada requisito de la consigna del TP3 (dónde está en el código y por qué) está en la sección 16.
