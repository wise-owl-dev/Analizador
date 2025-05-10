/**
 * Clase que representa un token en el análisis léxico
 */
public class Token {
    private String lexema; // Texto del token
    private String tipoToken; // Tipo de token
    private int linea; // Línea donde se encontró
    private int columna; // Columna donde se encontró

    /**
     * Constructor de la clase Token
     * 
     * @param lexema    Texto del token
     * @param tipoToken Tipo de token
     * @param linea     Línea donde se encontró
     * @param columna   Columna donde se encontró
     */
    public Token(String lexema, String tipoToken, int linea, int columna) {
        this.lexema = lexema;
        this.tipoToken = tipoToken;
        this.linea = linea;
        this.columna = columna;
    }

    // Getters
    public String getLexema() {
        return lexema;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-20s línea: %-4d columna: %-4d",
                tipoToken, "'" + lexema + "'", linea, columna);
    }
}