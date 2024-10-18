import java.util.ArrayList;

public class TablaPaginas {

    // Clase interna que representa una página con los bits R y M
    static class Page {
        int marcoReferencia;  // Número de la página virtual
        int referenced;       // Bit R
        int modified;         // Bit M

        public Page(int pageNumber) {
            this.marcoReferencia = pageNumber;
            this.referenced = 0;
            this.modified = 0;
        }

        @Override
        public String toString() {
            return "Page " + marcoReferencia + " [R: " + referenced + ", M: " + modified + "]";
        }
    }

    private static ArrayList<Page> frames;  // Lista de marcos en RAM
    private static int numFrames;           // Número de marcos disponibles en RAM
    private static int pageFaults;          // Contador de fallas de página
    private static int pageHits;            // Contador de hits

    // Inicializar la tabla de páginas con un número determinado de marcos
    public static void inicializarTabla(int numMarcos) {
        numFrames = numMarcos;
        frames = new ArrayList<>();
        pageFaults = 0;
        pageHits = 0;
    }

    // Simula el acceso a una página
    public static void accederPagina(int pageNumber, boolean isWrite) {
        for (Page page : frames) {
            if (page.marcoReferencia == pageNumber) {
                page.referenced = 1;  // Marcar como referenciada
                if (isWrite) page.modified = 1;  // Marcar como modificada si es escritura
                pageHits++;
                return;  // Hit
            }
        }

        pageFaults++;
        if (frames.size() >= numFrames) {
            reemplazarPagina();
        }

        Page newPage = new Page(pageNumber);
        newPage.referenced = 1;
        if (isWrite) newPage.modified = 1;
        frames.add(newPage);
    }

    // Algoritmo de reemplazo de página (NRU)
    private static void reemplazarPagina() {
        Page pageToReplace = null;

        for (Page page : frames) {
            if (page.referenced == 0 && page.modified == 0) {
                pageToReplace = page;
                break;
            }
        }

        if (pageToReplace == null) {
            for (Page page : frames) {
                if (page.referenced == 0 && page.modified == 1) {
                    pageToReplace = page;
                    break;
                }
            }
        }

        if (pageToReplace == null) {
            for (Page page : frames) {
                if (page.referenced == 1 && page.modified == 0) {
                    pageToReplace = page;
                    break;
                }
            }
        }

        if (pageToReplace == null) {
            pageToReplace = frames.get(0);
        }

        frames.remove(pageToReplace);
        System.out.println("Página reemplazada: " + pageToReplace);
        resetearBitsReferencia();
    }

    // Reiniciar los bits de referencia
    public static void resetearBitsReferencia() {
        for (Page page : frames) {
            page.referenced = 0;
        }
        System.out.println("Bits de referencia reseteados.");
    }

    public static int obtenerFallasPagina() {
        return pageFaults;
    }

    public static int obtenerHitsPagina() {
        return pageHits;
    }
}
