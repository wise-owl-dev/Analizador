package analizadorRobot;

/**
 * Clase que representa un token en el análisis léxico
 */
public class Token {
    private String lexeme; // Texto del token
    private String tokenType; // Tipo de token
    private int line; // Línea donde se encontró
    private int column; // Columna donde se encontró

    /**
     * Constructor de la clase Token
     * 
     * @param lexeme    Texto del token
     * @param tokenType Tipo de token
     * @param line      Línea donde se encontró
     * @param column    Columna donde se encontró
     */
    public Token(String lexeme, String tokenType, int line, int column) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.line = line;
        this.column = column;
    }

    // Getters
    public String getLexeme() {
        return lexeme;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-20s línea: %-4d columna: %-4d",
                tokenType, "'" + lexeme + "'", line, column);
    }
}