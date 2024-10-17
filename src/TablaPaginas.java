import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TablaPaginas {

    // Clase interna que representa una página con los bits R y M
    static class Page {
        int marcoReferencia;     // Número de la página virtual
        int referenced; // Bit R
        int modified;   // Bit M

        public Page(int pageNumber) {
            this.marcoReferencia = -1;
            this.referenced = 0;
            this.modified = 0;
        }

        @Override
        public String toString() {
            return "Page " + marcoReferencia + " [R: " + referenced + ", M: " + modified + "]";
        }
    }

    public static Page[] tablaPaginas; // Arreglo de objetos Page

    // Crear la tabla de páginas leyendo el archivo de referencias
    public static void crearTablaPaginas(String archivoReferencias) {

        try (BufferedReader br = new BufferedReader(new FileReader(archivoReferencias))) {
            String linea;
            int np = 0;  // Número de páginas virtuales

            // Leer el archivo para saber cuántas páginas hay
            while ((linea = br.readLine()) != null) {
                // Buscar la línea que define NP
                if (linea.startsWith("NP=")) {
                    np = Integer.parseInt(linea.split("=")[1]);  // Extraer el valor de NP
                    break;
                }
            }

            // Definir la tabla de páginas con el tamaño NP
            tablaPaginas = new Page[np];

            // Inicializar la tabla de páginas
            for (int i = 0; i < np; i++) {
                tablaPaginas[i] = new Page(i);  // Crear una nueva página con número de página i
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Tabla de páginas creada con tamaño: " + tablaPaginas.length);
    }


}
