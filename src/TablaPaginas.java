import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TablaPaginas {

    public static int[] tablaPaginas;

    public static void crearTablaPaginas (String archivoReferencias){

        try (BufferedReader br = new BufferedReader(new FileReader(archivoReferencias))) {
        String linea;
        int np = 0;  // Número de páginas virtuales

        // Leer el archivo para saber cuantas paginas hay
        while ((linea = br.readLine()) != null) {
            // Buscar la línea que define NP
            if (linea.startsWith("NP=")) {
                np = Integer.parseInt(linea.split("=")[1]);  // Extraer el valor de NP
                break;
            }
        }

        // Definir la tabla de páginas con el tamaño NP
        tablaPaginas = new int[np];

        // Inicializar la tabla de páginas
        for (int i = 0; i < np; i++) {
            tablaPaginas[i] = -1;  // Las páginas no están asignadas estan marcadas con -1
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    System.out.println("Tabla de páginas creada con tamaño: " + tablaPaginas.length);
}
    
}
