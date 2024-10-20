import java.io.*;
import java.util.ArrayList;

public class Simulator {

    private static ArrayList<RecursoMemoria> listaReferencias = new ArrayList<>();

    // Leer el contenido de un archivo de texto y devolverlo como un array de caracteres
    public static int leerArchivoTexto(String rutaArchivo, char[] contenidoMensaje) {
        int contadorCaracteres = 0;

        try (FileReader archivo = new FileReader(rutaArchivo);
             BufferedReader buffer = new BufferedReader(archivo)) {

            int codigoCaracter;
            while ((codigoCaracter = buffer.read()) != -1) {
                contenidoMensaje[contadorCaracteres] = (char) codigoCaracter;
                contadorCaracteres++;
            }
        } catch (IOException errorLectura) {
            errorLectura.printStackTrace();
        }

        return contadorCaracteres;
    }

    // Ocultar un mensaje en una imagen BMP
    public static void ocultarMensaje() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Nombre del archivo con la imagen a procesar: ");
            String rutaImagen = br.readLine();

            Imagen imagen = new Imagen(rutaImagen);

            System.out.println("Nombre del archivo con el mensaje a esconder: ");
            String rutaMensaje = br.readLine();

            int longitudMensaje = leerArchivoTexto(rutaMensaje, new char[8000]);
            char[] mensaje = new char[longitudMensaje];

            imagen.esconder(mensaje, longitudMensaje);
            imagen.escribirImagen("salida_" + rutaImagen);

            System.out.println("El mensaje ha sido escondido en la imagen: salida_" + rutaImagen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Extraer un mensaje oculto en una imagen BMP
    public static void extraerMensaje() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Nombre del archivo con el mensaje escondido: ");
            String rutaImagen = br.readLine();

            Imagen imagen = new Imagen(rutaImagen);
            int longitudMensaje = imagen.leerLongitud();
            char[] mensaje = new char[longitudMensaje];

            imagen.recuperar(mensaje, longitudMensaje);

            System.out.println("Nombre del archivo para almacenar el mensaje recuperado: ");
            String archivoSalida = br.readLine();

            try (FileWriter fw = new FileWriter(archivoSalida)) {
                fw.write(mensaje);
            }

            System.out.println("El mensaje ha sido guardado en: " + archivoSalida);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generar referencias de paginación para la imagen y el mensaje
    public static void generarReferencias(int tamanioPagina, String archivoImagen) {
        Imagen imagen = new Imagen(archivoImagen);

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

        escribirArchivoReferencias(tamanioPagina, altoImagen, anchoImagen, listaReferencias.size(), totalPaginas);
    }

    private static void generarReferenciasLongitudMensaje(int tamanioPagina, int anchoImagen) {
        for (int i = 0; i < 16; i++) {
            int paginaVirtual = i / tamanioPagina;
            int desplazamiento = i % tamanioPagina;

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
            int paginaVirtualMensaje = numPaginasImagen + (desplazamientoMensaje / tamanioPagina);
            listaReferencias.add(new RecursoMemoria(paginaVirtualMensaje, desplazamientoMensaje));
            contadorRef++;

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

    public static void escribirArchivoReferencias(int tamanioPagina, int alto, int ancho, int numReferencias, int totalPaginas) {
        try (FileWriter writer = new FileWriter("referencias.txt")) {
            writer.write("TP=" + tamanioPagina + "\n");
            writer.write("NF=" + alto + "\n");
            writer.write("NC=" + ancho + "\n");
            writer.write("NR=" + numReferencias + "\n");
            writer.write("NP=" + totalPaginas + "\n");
            for (RecursoMemoria ref : listaReferencias) {
                writer.write(ref.toString() + "\n");
            }
            System.out.println("Referencias guardadas en: referencias.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void calcularDatos(int numMarcosPagina, String archivoReferencias) {
        System.out.println("Calculando datos...");
        TablaPaginas.inicializarTabla(numMarcosPagina);

        try (BufferedReader br = new BufferedReader(new FileReader(archivoReferencias))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Imagen") || linea.startsWith("Mensaje")) {
                    String[] partes = linea.split(",");
                    int pagina = Integer.parseInt(partes[1]);
                    char tipo = partes[3].charAt(0);
                    boolean isWrite = (tipo == 'W');
                    TablaPaginas.accederPagina(pagina, isWrite);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int fallasPagina = TablaPaginas.obtenerFallasPagina();
        int hitsPagina = TablaPaginas.obtenerHitsPagina();
        System.out.println("Fallas de página: " + fallasPagina);
        System.out.println("Hits de página: " + hitsPagina);
        System.out.println("Porcentaje de hits: " + ((double) hitsPagina / (hitsPagina + fallasPagina)) * 100 + "%");
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
                        TablaPaginas.detenerResetearBitsThread(); 
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
                        TablaPaginas.inicializarTabla(numMarcosPagina); // Por ejemplo, 4 marcos de página
                        TablaPaginas.iniciarResetearBitsThread(2); 
                        TablaPaginas.iniciarActualizarEstadoThread();
                        calcularDatos(numMarcosPagina, archivoReferencias);
                        TablaPaginas.detenerResetearBitsThread(); // Detiene el thread
                        TablaPaginas.detenerActualizarEstadoThread();
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
