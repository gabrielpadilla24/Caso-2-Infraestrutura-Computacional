public class Referencia {

    String matrizVector;
    int paginaVirtual;
    int desplazamiento;
    char accion;

    public Referencia (String matrizVector, int paginaVirtual, int desplazamiento, char accion) {
        this.matrizVector = matrizVector;
        this.paginaVirtual = paginaVirtual;
        this.desplazamiento = desplazamiento;
        this.accion = accion;
    }    

    public String toString() {
       return matrizVector + ", Página: " + paginaVirtual + ", Desplazamiento: " + desplazamiento + ", Tipo: " + accion;
    }
}
