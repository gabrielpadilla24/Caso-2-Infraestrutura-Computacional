public class RecursoMemoria {
    private String tipo;
    private int row; 
    private int column; 
    private String color;
    private int pagina;
    private int offset;

    // Constructor para referencias de imagen
    public RecursoMemoria(int pagina, int offset, int row, int column, String color) {
        this.pagina = pagina;
        this.offset = offset;
        this.tipo = "Imagen";
        this.row = row;
        this.column = column;
        this.color = color;
    }

    // Constructor para referencias de mensaje
    public RecursoMemoria(int pagina, int offset) {
        this.pagina = pagina;
        this.offset = offset;
        this.tipo = "Mensaje";
    }

    // Método getter para obtener la página virtual
    public int obtenerPagina() {
        return this.pagina;
    }

    // Método getter para obtener el desplazamiento
    public int obtenerOffset() {
        return this.offset;
    }

    // Método getter para obtener el tipo de referencia (Imagen o Mensaje)
    public String obtenerTipo() {
        return this.tipo;
    }

    // Método para obtener la row de la imagen
    public int obtenerrow() {
        return this.row;
    }

    // Método para obtener la column de la imagen
    public int obtenercolumn() {
        return this.column;
    }

    // Método para obtener el canal de color de la imagen
    public String obtenerColor() {
        return this.color;
    }

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();

        // Si la referencia es para una imagen
        if ("Imagen".equals(tipo)) {
            resultado.append(tipo)
                    .append("[")
                    .append(row)
                    .append("][")
                    .append(column)
                    .append("].")
                    .append(color)
                    .append(",")
                    .append(pagina)
                    .append(",")
                    .append(offset)
                    .append(",R");
        } else {
            // Si la referencia es para un mensaje
            resultado.append(tipo)
                    .append("[")
                    .append(offset)
                    .append("],")
                    .append(pagina)
                    .append(",")
                    .append(offset)
                    .append(",W");
        }

        return resultado.toString();
    }
}
