# Sistema de Gestión de Inventario y Reposición — Distribuidora Sabor Sur S.R.L.

Proyecto integrador del **Trabajo Práctico 4** de Seminario de Práctica de Informática, Universidad Siglo 21
(alumno: Julián Azuaga). Es la versión final del prototipo desarrollado en los TP2 y TP3: agrega la interfaz
gráfica (Swing), organiza el código con el patrón **MVC + DAO**, completa la gestión de productos,
proveedores, ofertas y usuarios, y suma reportes CSV y una bitácora en archivo.

Implementa sobre MySQL todos los casos de uso definidos en el TP1 (UC-01 a UC-11).

## Estructura del proyecto

```
inventario-sabor-sur/
├── pom.xml
├── sabor_sur_db.sql                  (script de la base de datos: tablas, restricciones y datos de prueba)
├── config/
│   └── sabor_sur.properties.ejemplo  (plantilla con los datos de conexión)
└── src/
    ├── main/java/com/sabordelsur/inventario/
    │   ├── Main.java                 (abre la interfaz gráfica; con --consola, el menú de texto)
    │   ├── modelo/                   (MODELO: Usuario/Administrador/Operador, MovimientoStock y subclases,
    │   │                              Producto, Categoria, Proveedor, ProductoProveedor, PedidoReposicion,
    │   │                              ResumenInventario, interfaz Validable, enums)
    │   ├── vista/                    (VISTA: ventanas y paneles Swing, ModeloTabla, PanelBase, AppConsola)
    │   ├── controlador/              (CONTROLADOR: un controlador por caso de uso y FabricaControladores)
    │   ├── persistencia/             (DAO: interfaz CrudDAO, clase abstracta DAOBase, ConexionBD, un DAO por tabla)
    │   ├── archivos/                 (ReporteCSV y sus subclases, Bitacora, ConfiguracionBD)
    │   ├── excepciones/              (InventarioException y sus subclases)
    │   ├── estructuras/              (ColaEnlazada y PilaEnlazada, implementadas con nodos)
    │   ├── algoritmos/               (Ordenamiento: inserción y mezcla; Busqueda: binaria y lineal)
    │   └── seguridad/                (Hash: SHA-256 de las contraseñas)
    └── test/java/...                 (76 pruebas JUnit 5: 70 unitarias y 6 de integración con MySQL)
```

## Requisitos

- Java 11 o superior (JDK).
- Maven 3.8 o superior.
- MySQL 8.0.16 o superior (las restricciones `CHECK` del script se aplican desde esa versión).

## Puesta en marcha

1. Crear la base de datos (el script la elimina y la vuelve a crear con datos de prueba):

   ```bash
   mysql -u root -p < sabor_sur_db.sql
   ```

2. Copiar `config/sabor_sur.properties.ejemplo` como `config/sabor_sur.properties` y completar el usuario y
   la contraseña de MySQL. También se pueden usar las variables de entorno `SABOR_SUR_DB_URL`,
   `SABOR_SUR_DB_USER` y `SABOR_SUR_DB_PASSWORD`, que tienen prioridad sobre el archivo.

3. Compilar, ejecutar las pruebas y generar el jar ejecutable:

   ```bash
   mvn clean package
   ```

4. Ejecutar la aplicación desde la carpeta del proyecto (para que encuentre `config/`):

   ```bash
   java -jar target/inventario-sabor-sur-jar-with-dependencies.jar            # interfaz gráfica
   java -jar target/inventario-sabor-sur-jar-with-dependencies.jar --consola  # menú de consola
   ```

   También se puede abrir el proyecto en IntelliJ IDEA o Eclipse y ejecutar `Main.java`.

5. Iniciar sesión con alguno de los usuarios de prueba:

   | Usuario       | Contraseña       | Rol            |
   |---------------|------------------|----------------|
   | `propietario` | `propietario123` | Administrador  |
   | `operario1`   | `operario123`    | Operador       |
   | `operario2`   | `operario456`    | Operador       |

   Las pestañas dependen del rol. El Operador ve **Stock** y **Movimientos**. El Administrador ve **Stock**,
   **Pedidos de reposición**, **Comparar proveedores**, **Productos**, **Proveedores**, **Usuarios** y **Reportes**.

## Archivos que genera la aplicación

- `logs/bitacora.log`: una línea por operación (inicios de sesión, movimientos, altas, bajas, exportaciones).
- `reportes/stock-AAAAMMDD-HHMMSS.csv` y `reportes/movimientos-AAAAMMDD-HHMMSS.csv`: reportes separados por
  punto y coma, en UTF-8, que se abren directamente con Excel o LibreOffice.

Ninguno de los dos se sube al repositorio (ver `.gitignore`).

## Pruebas de integración

Las 6 pruebas de `PersistenciaIntegracionTest` trabajan contra la base real y solo se ejecutan si la variable de
entorno `SABOR_SUR_IT` vale `1`; si no, JUnit las omite. Cada prueba crea su propio producto y lo borra al terminar.

```bash
SABOR_SUR_IT=1 mvn test
```

## Relación con el resto de la entrega

- Diagramas de clases, de paquetes (MVC) y de secuencia, y modelo relacional: secciones 10, 11 y 17 del documento.
- Script SQL: sección 12 y archivo `sabor_sur_db.sql`.
- Explicación de los requisitos del TP3 (POO, excepciones, estructuras, algoritmos): sección 16.
- Explicación de los requisitos del TP4 (patrón de diseño, persistencia, excepciones de base de datos, clases
  abstractas e interfaces, arreglos y ArrayList, archivos, interfaz gráfica): sección 17.
