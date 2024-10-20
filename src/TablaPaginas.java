import java.util.ArrayList;
import java.util.Iterator;

public class TablaPaginas {

    static class Page {
        int marcoReferencia;
        int referenced;
        int modified;

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

    private static ArrayList<Page> frames;
    private static int numFrames;
    private static int pageFaults;
    private static int pageHits;

    // Thread para resetear bits de referencia
    private static class ResetearBitsThread extends Thread {
        private long intervalo;

        public ResetearBitsThread(long intervalo) {
            this.intervalo = intervalo;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(intervalo);
                    resetearBitsReferencia();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); 
                    break;
                }
            }
        }
    }

    // Thread para actualizar el estado de la tabla de páginas
    private static class ActualizarEstadoThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1); // Duerme por un milisegundo
                    actualizarEstadoTabla(); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); 
                    break; 
                }
            }
        }

        private void actualizarEstadoTabla() {
            // Lógica para actualizar el estado de la tabla de páginas y marcos de RAM
            synchronized (TablaPaginas.class) {
                for (Iterator<Page> iterator = frames.iterator(); iterator.hasNext();) {
                    Page page = iterator.next();

                    //System.out.println("Actualizando: " + page);

                }
            }
        }
    }

    private static ResetearBitsThread resetearBitsThread; // Declaración del thread para resetear bits
    private static ActualizarEstadoThread actualizarEstadoThread; // Declaración del thread para actualizar estado

    public static synchronized void inicializarTabla(int numMarcos) {
        numFrames = numMarcos;
        frames = new ArrayList<>();
        pageFaults = 0;
        pageHits = 0;
    }

    public static synchronized void accederPagina(int pageNumber, boolean isWrite) {
        for (Page page : frames) {
            if (page.marcoReferencia == pageNumber) {
                page.referenced = 1;
                if (isWrite) page.modified = 1;
                pageHits++;
                return;
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

    private static synchronized void reemplazarPagina() {
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
        //System.out.println("Página reemplazada: " + pageToReplace);
    }

    public static synchronized void resetearBitsReferencia() {
        for (Page page : frames) {
            page.referenced = 0;
        }
        //System.out.println("Bits de referencia reseteados.");
    }

    public static synchronized int obtenerFallasPagina() {
        return pageFaults;
    }

    public static synchronized int obtenerHitsPagina() {
        return pageHits;
    }

    // Método para iniciar el thread que resetea bits
    public static void iniciarResetearBitsThread(long intervalo) {
        resetearBitsThread = new ResetearBitsThread(intervalo);
        resetearBitsThread.start();
        System.out.println("Iniciando thread de reseteo de bits.");
    }

    // Método para detener el thread de reseteo
    public static void detenerResetearBitsThread() {
        if (resetearBitsThread != null) {
            resetearBitsThread.interrupt(); // Interrumpe el thread
            System.out.println("Deteniendo thread de reseteo de bits.");
        }
    }

    // Método para iniciar el thread que actualiza el estado
    public static void iniciarActualizarEstadoThread() {
        System.out.println("Iniciando thread de actualización de estado.");
        actualizarEstadoThread = new ActualizarEstadoThread();
        actualizarEstadoThread.start();
    }

    // Método para detener el thread de actualización
    public static void detenerActualizarEstadoThread() {
        if (actualizarEstadoThread != null) {
            actualizarEstadoThread.interrupt(); // Interrumpe el thread
            System.out.println("Deteniendo thread de actualización de estado.");
        }
    }
}
