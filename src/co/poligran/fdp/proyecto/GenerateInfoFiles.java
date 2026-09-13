package co.poligran.fdp.proyecto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera, de manera pseudoaleatoria, los archivos planos que servirán como
 * entrada para el programa principal del proyecto (clase {@code main}), el
 * cual se encargará, en una entrega posterior, de leer, organizar y reportar
 * la información de ventas, vendedores y productos.
 *
 * <p>
 * Esta clase corresponde a la <b>Entrega 1 (Semana 3)</b> del proyecto
 * grupal del módulo <i>Conceptos Fundamentales de Programación</i>
 * ("Generación y clasificación de datos"). Ninguno de los métodos aquí
 * definidos solicita información al usuario: toda la configuración se
 * controla mediante las constantes declaradas en la clase.
 * </p>
 *
 * <p>
 * Al ejecutarse, {@link #main(String[])} genera dentro de la carpeta
 * {@value #CARPETA_SALIDA} (ubicada en la raíz del proyecto, tal como lo
 * exige el enunciado) los siguientes archivos:
 * </p>
 * <ul>
 * <li>Un archivo con el catálogo de productos ({@value #ARCHIVO_PRODUCTOS}).</li>
 * <li>Un archivo con la información de los vendedores ({@value #ARCHIVO_VENDEDORES}).</li>
 * <li>Un archivo de ventas por cada vendedor generado.</li>
 * </ul>
 *
 * @author Joha
 * @version 1.0
 */
public class GenerateInfoFiles {

    /** Carpeta, dentro del proyecto, donde se almacenan todos los archivos generados. */
    private static final String CARPETA_SALIDA = "archivos_generados";

    /** Nombre del archivo con la información de todos los vendedores. */
    private static final String ARCHIVO_VENDEDORES = "vendedores.txt";

    /** Nombre del archivo con la información de todos los productos. */
    private static final String ARCHIVO_PRODUCTOS = "productos.txt";

    /** Tipos de documento de identidad válidos usados para generar los datos de prueba. */
    private static final String[] TIPOS_DOCUMENTO = { "CC", "CE", "TI", "PA" };

    /** Banco de nombres reales usado para generar vendedores de manera coherente. */
    private static final String[] NOMBRES = {
            "Carlos", "Maria", "Juan", "Laura", "Andres", "Camila", "Diego", "Valentina",
            "Santiago", "Mariana", "Felipe", "Daniela", "Sebastian", "Paula", "Julian", "Natalia"
    };

    /** Banco de apellidos reales usado para generar vendedores de manera coherente. */
    private static final String[] APELLIDOS = {
            "Gomez", "Rodriguez", "Martinez", "Lopez", "Garcia", "Perez", "Sanchez", "Ramirez",
            "Torres", "Diaz", "Vargas", "Castro", "Ortiz", "Rojas", "Moreno", "Suarez"
    };

    /** Precio mínimo, en pesos, usado al generar productos pseudoaleatorios. */
    private static final int PRECIO_MINIMO = 1000;

    /** Precio máximo, en pesos, usado al generar productos pseudoaleatorios. */
    private static final int PRECIO_MAXIMO = 200000;

    /** Cantidad mínima vendida de un producto en una línea de venta pseudoaleatoria. */
    private static final int CANTIDAD_MINIMA_VENDIDA = 1;

    /** Cantidad máxima vendida de un producto en una línea de venta pseudoaleatoria. */
    private static final int CANTIDAD_MAXIMA_VENDIDA = 20;

    /** Generador de números pseudoaleatorios reutilizado por toda la clase. */
    private static final Random GENERADOR_ALEATORIO = new Random();

    /**
     * Cantidad de productos disponibles en el último catálogo generado por
     * {@link #createProductsFile(int)}. Se usa como referencia para que
     * {@link #createSalesMenFile(int, String, long)} genere identificadores
     * de producto (IDProducto) que realmente existan en el catálogo vigente.
     * Tiene un valor por defecto razonable por si se invoca antes de generar
     * el catálogo de productos.
     */
    private static int cantidadProductosDisponibles = 10;

    /**
     * Punto de entrada del programa.
     *
     * <p>
     * Genera, en la carpeta {@value #CARPETA_SALIDA} dentro del proyecto, el
     * catálogo de productos, la información de los vendedores y, para cada
     * vendedor, un archivo plano con sus ventas pseudoaleatorias. Estos
     * archivos son la entrada que usará el programa principal (clase
     * {@code main}) en la siguiente entrega del proyecto.
     * </p>
     *
     * <p>
     * El programa no solicita ninguna información al usuario y, al terminar,
     * muestra un mensaje de finalización exitosa o, si algo falla, un
     * mensaje de error describiendo la causa.
     * </p>
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        // Configuración de esta ejecución: cantidad de vendedores, productos
        // y rango de ventas por vendedor a generar.
        final int cantidadVendedores = 8;
        final int cantidadProductos = 15;
        final int minVentasPorVendedor = 3;
        final int maxVentasPorVendedor = 10;

        try {
            crearCarpetaSalidaSiNoExiste();

            createProductsFile(cantidadProductos);
            List<Vendedor> vendedores = createSalesManInfoFile(cantidadVendedores);

            for (Vendedor vendedor : vendedores) {
                int ventasAleatorias = numeroAleatorioEntre(minVentasPorVendedor, maxVentasPorVendedor);
                createSalesMenFile(ventasAleatorias, vendedor.nombreCompleto(), vendedor.numeroDocumento);
            }

            System.out.println("Generacion de archivos finalizada exitosamente. Se generaron "
                    + cantidadVendedores + " vendedores, " + cantidadProductos
                    + " productos y sus respectivos archivos de ventas en la carpeta \""
                    + CARPETA_SALIDA + "\".");

        } catch (IOException error) {
            System.err.println("Error generando los archivos de entrada del proyecto: " + error.getMessage());
        }
    }

    /**
     * Crea, en la carpeta de salida del proyecto, un archivo plano con las
     * ventas pseudoaleatorias de un único vendedor.
     *
     * <p>
     * El archivo generado sigue el formato exigido por el enunciado (una
     * venta por línea):
     * </p>
     *
     * <pre>
     * TipoDocumentoVendedor;NúmeroDocumentoVendedor
     * IDProducto1;CantidadProducto1Vendido;
     * IDProducto2;CantidadProducto2Vendido;
     * </pre>
     *
     * @param randomSalesCount cantidad de líneas de venta (productos vendidos) a generar
     * @param name             nombre completo del vendedor; se usa para construir un
     *                         nombre de archivo legible
     * @param id               número de documento del vendedor; se usa como encabezado
     *                         del archivo y como parte del nombre de archivo
     * @throws IOException si ocurre un error al escribir el archivo en disco
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        crearCarpetaSalidaSiNoExiste();

        String tipoDocumento = TIPOS_DOCUMENTO[GENERADOR_ALEATORIO.nextInt(TIPOS_DOCUMENTO.length)];
        String nombreArchivo = "ventas_" + id + "_" + normalizarParaNombreArchivo(name) + ".txt";
        File archivo = new File(CARPETA_SALIDA, nombreArchivo);

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            escritor.write(tipoDocumento + ";" + id);
            escritor.newLine();

            for (int i = 0; i < randomSalesCount; i++) {
                int idProducto = numeroAleatorioEntre(1, cantidadProductosDisponibles);
                int cantidadVendida = numeroAleatorioEntre(CANTIDAD_MINIMA_VENDIDA, CANTIDAD_MAXIMA_VENDIDA);

                escritor.write(idProducto + ";" + cantidadVendida + ";");
                escritor.newLine();
            }
        }
    }

    /**
     * Crea, en la carpeta de salida del proyecto, el archivo plano
     * {@value #ARCHIVO_PRODUCTOS} con información pseudoaleatoria de
     * productos.
     *
     * <p>
     * El archivo generado sigue el formato exigido por el enunciado:
     * </p>
     *
     * <pre>
     * IDProducto1;NombreProducto1;PrecioPorUnidadProducto1
     * IDProducto2;NombreProducto2;PrecioPorUnidadProducto2
     * </pre>
     *
     * <p>
     * Los identificadores de producto generados aquí (1 .. {@code productsCount})
     * quedan disponibles para que {@link #createSalesMenFile(int, String, long)}
     * los use al construir las ventas de cada vendedor, de manera que los
     * archivos de ventas siempre referencien productos que existen en el
     * catálogo vigente.
     * </p>
     *
     * @param productsCount cantidad de productos a generar
     * @return la lista de productos generados
     * @throws IOException si ocurre un error al escribir el archivo en disco
     */
    public static List<Producto> createProductsFile(int productsCount) throws IOException {
        crearCarpetaSalidaSiNoExiste();

        List<Producto> productos = new ArrayList<>();
        File archivo = new File(CARPETA_SALIDA, ARCHIVO_PRODUCTOS);

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            for (int idProducto = 1; idProducto <= productsCount; idProducto++) {
                String nombreProducto = "Producto" + idProducto;
                int precioPorUnidad = numeroAleatorioEntre(PRECIO_MINIMO, PRECIO_MAXIMO);

                productos.add(new Producto(idProducto, nombreProducto, precioPorUnidad));

                escritor.write(idProducto + ";" + nombreProducto + ";" + precioPorUnidad);
                escritor.newLine();
            }
        }

        cantidadProductosDisponibles = Math.max(productsCount, 1);
        return productos;
    }

    /**
     * Crea, en la carpeta de salida del proyecto, el archivo plano
     * {@value #ARCHIVO_VENDEDORES} con información pseudoaleatoria —pero
     * coherente— de vendedores, tomando nombres y apellidos de bancos de
     * nombres reales.
     *
     * <p>
     * El archivo generado sigue el formato exigido por el enunciado (un
     * vendedor por línea):
     * </p>
     *
     * <pre>
     * TipoDocumento;NúmeroDocumento;NombresVendedor1;ApellidosVendedor1
     * TipoDocumento;NúmeroDocumento;NombresVendedor2;ApellidosVendedor2
     * </pre>
     *
     * @param salesmanCount cantidad de vendedores a generar
     * @return la lista de vendedores generados, usada luego por
     *         {@link #main(String[])} para crear el archivo de ventas de cada uno
     * @throws IOException si ocurre un error al escribir el archivo en disco
     */
    public static List<Vendedor> createSalesManInfoFile(int salesmanCount) throws IOException {
        crearCarpetaSalidaSiNoExiste();

        List<Vendedor> vendedores = new ArrayList<>();
        File archivo = new File(CARPETA_SALIDA, ARCHIVO_VENDEDORES);

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            for (int i = 0; i < salesmanCount; i++) {
                String tipoDocumento = TIPOS_DOCUMENTO[GENERADOR_ALEATORIO.nextInt(TIPOS_DOCUMENTO.length)];
                long numeroDocumento = numeroDocumentoAleatorio();
                String nombres = NOMBRES[GENERADOR_ALEATORIO.nextInt(NOMBRES.length)];
                String apellidos = APELLIDOS[GENERADOR_ALEATORIO.nextInt(APELLIDOS.length)];

                Vendedor vendedor = new Vendedor(tipoDocumento, numeroDocumento, nombres, apellidos);
                vendedores.add(vendedor);

                escritor.write(tipoDocumento + ";" + numeroDocumento + ";" + nombres + ";" + apellidos);
                escritor.newLine();
            }
        }

        return vendedores;
    }

    // -----------------------------------------------------------------
    // Métodos de apoyo (privados)
    // -----------------------------------------------------------------

    /**
     * Genera un número entero pseudoaleatorio dentro del rango [minimo, maximo], ambos incluidos.
     *
     * @param minimo valor mínimo posible (inclusive)
     * @param maximo valor máximo posible (inclusive)
     * @return número entero pseudoaleatorio entre minimo y maximo
     */
    private static int numeroAleatorioEntre(int minimo, int maximo) {
        return minimo + GENERADOR_ALEATORIO.nextInt((maximo - minimo) + 1);
    }

    /**
     * Genera un número de documento pseudoaleatorio de 8 dígitos.
     *
     * @return número de documento pseudoaleatorio
     */
    private static long numeroDocumentoAleatorio() {
        return 10_000_000L + GENERADOR_ALEATORIO.nextInt(90_000_000);
    }

    /**
     * Reemplaza espacios y caracteres no alfanuméricos de un nombre para que
     * pueda usarse de forma segura como parte de un nombre de archivo.
     *
     * @param nombre nombre original
     * @return versión del nombre segura para usar en el sistema de archivos
     */
    private static String normalizarParaNombreArchivo(String nombre) {
        return nombre.trim().replaceAll("[^a-zA-Z0-9]+", "_");
    }

    /**
     * Crea la carpeta de salida del proyecto si todavía no existe.
     *
     * @throws IOException si la carpeta no existe y no pudo ser creada
     */
    private static void crearCarpetaSalidaSiNoExiste() throws IOException {
        File carpeta = new File(CARPETA_SALIDA);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No fue posible crear la carpeta de salida: " + carpeta.getAbsolutePath());
        }
    }

    /**
     * Representa la información básica de un vendedor generada de manera
     * pseudoaleatoria: su tipo y número de documento, y su nombre completo.
     */
    static class Vendedor {

        final String tipoDocumento;
        final long numeroDocumento;
        final String nombres;
        final String apellidos;

        Vendedor(String tipoDocumento, long numeroDocumento, String nombres, String apellidos) {
            this.tipoDocumento = tipoDocumento;
            this.numeroDocumento = numeroDocumento;
            this.nombres = nombres;
            this.apellidos = apellidos;
        }

        /**
         * @return el nombre completo del vendedor (nombres y apellidos)
         */
        String nombreCompleto() {
            return nombres + " " + apellidos;
        }
    }

    /**
     * Representa la información básica de un producto generada de manera
     * pseudoaleatoria: su identificador, nombre y precio por unidad.
     */
    static class Producto {

        final int idProducto;
        final String nombreProducto;
        final int precioPorUnidad;

        Producto(int idProducto, String nombreProducto, int precioPorUnidad) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.precioPorUnidad = precioPorUnidad;
        }
    }
}
