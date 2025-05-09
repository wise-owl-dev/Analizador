package main.java.robotcontrol;

/**
 * Enumeración de todos los tokens posibles en el lenguaje de control robótico.
 * Cada token tiene un nombre y un valor asociado.
 */
public enum Token {
    // Palabras reservadas
    ROBOT("robot", 1),
    INICIAR("iniciar", 2),
    DETENER("detener", 3),

    // Métodos
    BASE("base", 4),
    CUERPO("cuerpo", 5),
    GARRA("garra", 6),
    VELOCIDAD("velocidad", 7),
    ABRIR_GARRA("abrirGarra", 8),
    CERRAR_GARRA("cerrarGarra", 9),
    REPETIR("repetir", 10),

    // Símbolos
    PUNTO(".", 11),
    IGUAL("=", 12),
    PARENTESIS_ABIERTO("(", 13),
    PARENTESIS_CERRADO(")", 14),
    LLAVE_ABIERTA("{", 15),
    LLAVE_CERRADA("}", 16),

    // Otros
    IDENTIFICADOR("IDENTIFICADOR", 17),
    NUMERO("NUMERO", 18),

    // Fin de instrucción
    SEMI_COLON(";", 19),

    // Errores
    ERROR("ERROR", 20),

    // Fin de entrada
    EOF("EOF", 21);

    private final String name;
    private final int value;

    Token(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    /**
     * Busca un token por su nombre.
     * 
     * @param name Nombre del token
     * @return Token correspondiente o null si no existe
     */
    public static Token findByName(String name) {
        for (Token token : Token.values()) {
            if (token.name.equalsIgnoreCase(name)) {
                return token;
            }
        }
        return null;
    }
}