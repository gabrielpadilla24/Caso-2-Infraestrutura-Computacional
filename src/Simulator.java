import java.io.*;
import java.util.ArrayList;

public class Simulator {

    private static ArrayList<RecursoMemoria> listaReferencias = new ArrayList<>();

    // Leer el contenido de un archivo de texto y devolverlo como un array de caracteres
    public static int leerArchivoTexto(String rutaArchivo, char[] contenidoMensaje) {
        int contadorCaracteres = 0;
    
    try {
        // Abrimos el archivo usando un FileReader y BufferedReader
        try (FileReader archivo = new FileReader(rutaArchivo);
             BufferedReader buffer = new BufferedReader(archivo)) {
        
            int codigoCaracter;
        
            // Usamos un ciclo for para procesar la lectura del archivo
            for (codigoCaracter = buffer.read(); codigoCaracter != -1; codigoCaracter = buffer.read()) {
                // Convertimos el código del carácter en char y lo almacenamos en el arreglo
                contenidoMensaje[contadorCaracteres] = (char) codigoCaracter;
                contadorCaracteres++;
            }
        }
        
    } catch (IOException errorLectura) {
        // Se captura cualquier excepción y se muestra el error
        errorLectura.printStackTrace();
    }
    
    // Retornamos la cantidad de caracteres leídos
    return contadorCaracteres;
}

//YA LISTO
// Ocultar un mensaje en una imagen BMP
public static void ocultarMensaje() {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    
    try {
        System.out.println("Nombre del archivo con la imagen a procesar: ");
        String rutaImagen = br.readLine();

        Imagen imagen = new Imagen(rutaImagen);

        System.out.println("Nombre del archivo con el mensaje a esconder: ");
        String rutaMensaje = br.readLine();

        // Leer el archivo con el mensaje y convertirlo a un array de caracteres
        int longitudMensaje = leerArchivoTexto(rutaMensaje, new char[8000]);
        char[] mensaje = new char[longitudMensaje];

        // Ocultar el mensaje en la imagen
        imagen.esconder(mensaje, longitudMensaje);

        // Guardar la imagen con el mensaje escondido
        imagen.escribirImagen("salida_" + rutaImagen);

        System.out.println("El mensaje ha sido escondido en la imagen: salida_" + rutaImagen);
        br.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

//YA LISTO
// Extraer un mensaje oculto en una imagen BMP
public static void extraerMensaje() {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    
    try {
        System.out.println("Nombre del archivo con el mensaje escondido: ");
        String rutaImagen = br.readLine();

        Imagen imagen = new Imagen(rutaImagen);
        int longitudMensaje = imagen.leerLongitud(); // Obtener la longitud del mensaje escondido
        char[] mensaje = new char[longitudMensaje]; // Crear un array de caracteres para almacenar el mensaje

        // Recuperar el mensaje de la imagen
        imagen.recuperar(mensaje, longitudMensaje);

        System.out.println("Nombre del archivo para almacenar el mensaje recuperado: ");
        String archivoSalida = br.readLine();

        // Guardar el mensaje recuperado en un archivo de texto
        try (FileWriter fw = new FileWriter(archivoSalida)) {
            fw.write(mensaje);
        }

        System.out.println("El mensaje ha sido guardado en: " + archivoSalida);
        br.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Generar referencias de paginación para la imagen y el mensaje
public static void generarReferencias(int tamanioPagina, String archivoImagen) {
    Imagen imagen = new Imagen(archivoImagen);

    // Datos de la imagen
    int anchoImagen = imagen.getAncho();
    int altoImagen = imagen.getAlto();
    int totalBytesImagen = anchoImagen * altoImagen * 3;
    int longitudMensaje = imagen.leerLongitud();
    int totalReferencias = (longitudMensaje * 17) + 16;
    int numPaginasImagen = (int) Math.ceil((double) totalBytesImagen / tamanioPagina);
    int numPaginasMensaje = (int) Math.ceil((double) longitudMensaje / tamanioPagina);
    int totalPaginas = numPaginasImagen + numPaginasMensaje;

    generarReferenciasLongitudMensaje(tamanioPagina, anchoImagen);
    generarReferenciasAlternadas(tamanioPagina, anchoImagen, longitudMensaje, numPaginasImagen, totalReferencias);

    // Guardar las referencias en "referencias.txt"
    escribirArchivoReferencias(tamanioPagina, altoImagen, anchoImagen, listaReferencias.size(), totalPaginas);
}


private static void generarReferenciasLongitudMensaje(int tamanioPagina, int anchoImagen) {
    int desplazamiento;
    for (int i = 0; i < 16; i++) {
        int paginaVirtual = i / tamanioPagina;
        desplazamiento = i % tamanioPagina;

        int fila = i / (anchoImagen * 3);
        int columna = (i % (anchoImagen * 3)) / 3;
        String canalColor = (i % 3 == 0) ? "R" : (i % 3 == 1) ? "G" : "B";

        listaReferencias.add(new RecursoMemoria(paginaVirtual, desplazamiento, fila, columna, canalColor));
    }
}

private static void generarReferenciasAlternadas(int tamanioPagina, int anchoImagen, int longitudMensaje, int numPaginasImagen, int totalReferencias) {
    int contadorRef = 16;
    int bitIndex = 0;
    int desplazamientoMensaje = 0;

    while (contadorRef < totalReferencias) {
        // Escribir en el mensaje (W)
        int paginaVirtualMensaje = numPaginasImagen + (desplazamientoMensaje / tamanioPagina);
        listaReferencias.add(new RecursoMemoria(paginaVirtualMensaje, desplazamientoMensaje));
        contadorRef++;

        // Leer de la imagen (R)
        int posicionByte = 16 + bitIndex;
        int desplazamiento = posicionByte % tamanioPagina;
        int paginaVirtual = posicionByte / tamanioPagina;
        int fila = posicionByte / (anchoImagen * 3);
        int columna = (posicionByte % (anchoImagen * 3)) / 3;
        String canalColor = (posicionByte % 3 == 0) ? "R" : (posicionByte % 3 == 1) ? "G" : "B";

        listaReferencias.add(new RecursoMemoria(paginaVirtual, desplazamiento, fila, columna, canalColor));
        bitIndex++;
        contadorRef++;

        if (bitIndex % 8 == 0 && contadorRef < totalReferencias) {
            listaReferencias.add(new RecursoMemoria(paginaVirtualMensaje, desplazamientoMensaje));
            desplazamientoMensaje++;
            contadorRef++;
        }
    }
}

// Método para escribir las referencias generadas en un archivo
public static void escribirArchivoReferencias(int tamanioPagina, int alto, int ancho, int numReferencias, int totalPaginas) {
    String archivoSalida = "referencias.txt"; // El archivo siempre será "referencias.txt"
    try (FileWriter writer = new FileWriter(archivoSalida)) {
        writer.write("TP=" + tamanioPagina + "\n");
        writer.write("NF=" + alto + "\n");
        writer.write("NC=" + ancho + "\n");
        writer.write("NR=" + numReferencias + "\n");
        writer.write("NP=" + totalPaginas + "\n");
        for (RecursoMemoria ref : listaReferencias) {
            writer.write(ref.toString() + "\n");
        }
        System.out.println("Referencias guardadas en: " + archivoSalida);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static void calcularDatos(int numMarcosPagina, String archivoReferencias) {
    System.out.println("Calculando datos...");
    System.out.println("Número de marcos de página: " + numMarcosPagina);
    System.out.println("Archivo de referencias: " + archivoReferencias);
    cargarReferencias(archivoReferencias);
}

public static void cargarReferencias(String archivoReferencias) {
    ArrayList<Referencia> referencias = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivoReferencias))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Saltar las líneas que no contienen referencias
                if (linea.startsWith("Imagen") || linea.startsWith("Mensaje")) {
                    // Dividir la línea en partes usando la coma como delimitador
                    String[] partes = linea.split(",");
                    String nombre = partes[0];  // Matriz o vector y posicion correspondiente
                    int pagina = Integer.parseInt(partes[1]);  // Pagina virtual correspondiente
                    int desplazamiento = Integer.parseInt(partes[2]);  // Desplazamiento en la pagina virtual
                    char tipo = partes[3].charAt(0);  // 'R' o 'W'

                    // Crear una nueva referencia y añadirla al array
                    Referencia ref = new Referencia(nombre, pagina, desplazamiento, tipo);
                    referencias.add(ref);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("\nReferencias cargadas: " + referencias.size()+"\n");
    }
        
    public static void crearTablaPaginas(String archivoReferencias) {
        TablaPaginas.crearTablaPaginas(archivoReferencias);
    }


    // Método principal del simulador
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            boolean continuar = true;

            while (continuar) {
                System.out.println("============================");
                System.out.println("Seleccione una opción:");
                System.out.println("1. Generar referencias de paginación.");
                System.out.println("2. Ocultar mensaje en imagen BMP.");
                System.out.println("3. Extraer mensaje de imagen BMP.");
                System.out.println("4. Calcular datos buscados.");
                System.out.println("5. Salir.");
                System.out.println("============================");

                int opcion = Integer.parseInt(br.readLine());

                switch (opcion) {
                    case 1:
                        System.out.println("Ingrese el tamaño de página (en bytes): ");
                        int tamanioPagina = Integer.parseInt(br.readLine());
                        System.out.println("Ingrese el nombre del archivo de imagen BMP: ");
                        String archivoImagen = br.readLine();
                        generarReferencias(tamanioPagina, "./archivos/" + archivoImagen);
                        break;
                    case 2:
                        ocultarMensaje();
                        break;
                    case 3:
                        extraerMensaje();
                        break;
                    case 4:
                        System.out.println("Ingrese el número de marcos de página:");
                        int numMarcosPagina = Integer.parseInt(br.readLine());
                        System.out.println("Ingrese el nombre del archivo de referencias: ");
                        String archivoReferencias = br.readLine();
                        calcularDatos (numMarcosPagina, archivoReferencias);
                        crearTablaPaginas(archivoReferencias);
                        break;
  
                    case 5:
                        continuar = false;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}