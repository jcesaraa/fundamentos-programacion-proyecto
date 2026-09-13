# Proyecto Ventas — Entrega 1 (Semana 3)

Módulo: **Conceptos Fundamentales de Programación**
Proyecto: **Generación y clasificación de datos**
Entrega: **Entrega 1 — Semana 3**

## Qué contiene esta entrega

Según las instrucciones de la Entrega 1 (Semana 3), en esta primera entrega
solo se pide el diseño e implementación de la clase `GenerateInfoFiles`,
capaz de generar los archivos planos pseudoaleatorios que usará como entrada
el programa principal del proyecto (clase `main`, que se entregará en la
Entrega 2).

Este proyecto de Eclipse contiene una única clase con método `main`:

- `co.poligran.fdp.proyecto.GenerateInfoFiles`

Al ejecutarse, genera dentro de la carpeta `archivos_generados/` (creada
automáticamente en la raíz del proyecto):

- `productos.txt`: catálogo de productos pseudoaleatorio.
- `vendedores.txt`: información de vendedores pseudoaleatoria pero coherente
  (nombres y apellidos tomados de bancos de nombres reales).
- `ventas_<documento>_<nombre>.txt`: un archivo de ventas por cada vendedor
  generado, con el formato exigido por el enunciado.

El programa no solicita ninguna información al usuario y, al finalizar,
muestra un mensaje de éxito por consola o un mensaje de error si algo falla.

## Métodos requeridos

- `createSalesMenFile(int randomSalesCount, String name, long id)`
- `createProductsFile(int productsCount)`
- `createSalesManInfoFile(int salesmanCount)`

Los tres métodos son públicos y estáticos, y pueden invocarse de manera
independiente (cada uno crea su propia carpeta de salida si no existe).

## Cómo abrir y ejecutar el proyecto en Eclipse

1. Abrir Eclipse (versión **Eclipse IDE for Java Developers**).
2. `File > Import... > General > Existing Projects into Workspace`.
3. Seleccionar esta carpeta (`ProyectoVentas-Entrega1`) como raíz de búsqueda.
4. Verificar que el proyecto use un JRE/JDK **Java 8** (`Properties > Java
   Build Path > Libraries`, o `Properties > Java Compiler`).
5. Clic derecho sobre `GenerateInfoFiles.java` → `Run As > Java Application`.
6. Revisar la carpeta `archivos_generados/` que aparece en la raíz del
   proyecto tras la ejecución.

## Cómo compilar y ejecutar por línea de comandos (alternativa)

```bash
mkdir bin
javac -d bin -encoding UTF-8 src/co/poligran/fdp/proyecto/GenerateInfoFiles.java
java -cp bin co.poligran.fdp.proyecto.GenerateInfoFiles
```
