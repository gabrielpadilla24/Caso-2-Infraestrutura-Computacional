import java.util.*;

class Page {
    int pageNumber;
    boolean referenced; // Bit R
    boolean modified;   // Bit M

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
        this.referenced = false;
        this.modified = false;
    }

    public String toString() {
        return "Page " + pageNumber + " [Referenced: " + referenced + ", Modified: " + modified + "]";
    }
}

class NRU {
    private List<Page> pageTable;
    private int maxPages;

    public NRU(int maxPages) {
        this.maxPages = maxPages;
        this.pageTable = new ArrayList<>();
    }

    // Simula acceso a una página y actualiza los bits R y M
    public void accessPage(int pageNumber, boolean isModified) {
        for (Page page : pageTable) {
            if (page.pageNumber == pageNumber) {
                page.referenced = true; // Se marca la página como referenciada
                if (isModified) {
                    page.modified = true; // Si fue modificada, se marca también el bit M
                }
                return; // Salimos porque la página ya está en la tabla
            }
        }
        // Si no se encuentra la página en la tabla, se añade (si hay espacio)
        if (pageTable.size() < maxPages) {
            Page newPage = new Page(pageNumber);
            newPage.referenced = true; // Al acceder, se marca como referenciada
            if (isModified) {
                newPage.modified = true;
            }
            pageTable.add(newPage);
        } else {
            // Si no hay espacio, reemplazamos una página
            replacePage();
            accessPage(pageNumber, isModified); // Intentamos de nuevo añadir la página
        }
    }

    private void replacePage() {
        // Buscar la página a reemplazar según NRU
        // Se clasifican las páginas en cuatro categorías: 0, 1, 2, 3 basadas en los bits R y M.
        // Clase 0: R=0, M=0; Clase 1: R=0, M=1; Clase 2: R=1, M=0; Clase 3: R=1, M=1.

        Page pageToReplace = null;

        // Intentamos buscar primero una página en la clase 0
        for (Page page : pageTable) {
            if (!page.referenced && !page.modified) {
                pageToReplace = page;
                break;
            }
        }
        // Si no encontramos en clase 0, buscamos en clase 1
        if (pageToReplace == null) {
            for (Page page : pageTable) {
                if (!page.referenced && page.modified) {
                    pageToReplace = page;
                    break;
                }
            }
        }
        // Si no encontramos en clase 1, buscamos en clase 2
        if (pageToReplace == null) {
            for (Page page : pageTable) {
                if (page.referenced && !page.modified) {
                    pageToReplace = page;
                    break;
                }
            }
        }
        // Si no encontramos en clase 2, usamos una página de clase 3
        if (pageToReplace == null) {
            pageToReplace = pageTable.get(0); // Reemplazamos la primera en la tabla (arbitrario)
        }

        // Al final, eliminamos la página seleccionada
        pageTable.remove(pageToReplace);
        System.out.println("Replaced: " + pageToReplace);

        // Reiniciamos los bits de referencia (R) de todas las páginas después de un reemplazo
        resetReferencedBits();
    }

    // Reiniciar los bits de referencia
    private void resetReferencedBits() {
        for (Page page : pageTable) {
            page.referenced = false;
        }
        System.out.println("Referenced bits reset.");
    }

    // Imprimir el estado actual de la tabla de páginas
    public void printPageTable() {
        System.out.println("Current Page Table:");
        for (Page page : pageTable) {
            System.out.println(page);
        }
    }
}
