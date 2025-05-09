package main.java.robotcontrol;

import java.io.StringReader;
import java_cup.runtime.Symbol;
import java.util.List;

/**
 * Clase principal para el análisis sintáctico
 */
public class AnalizadorSintactico {
    private String input;
    private parser syntacticParser;
    private LexerCup lexer;

    /**
     * Constructor que inicializa el analizador sintáctico
     * @param input Código de entrada a analizar
     */
    public SyntaxAnalyzer(String input) {
        this.input = input;
        this.lexer = new LexerCup(new StringReader(input));
        this.syntacticParser = new parser(lexer);
    }

    /**
     * Realiza el análisis sintáctico
     * 
     * @return true si no hay errores, false en caso contrario
     */
    public boolean analyze() {
        try {
            syntacticParser.parse();
            return syntacticParser.getSyntaxErrors().isEmpty();
        } catch (Exception e) {
            System.err.println("Error en el análisis sintáctico: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene la tabla de símbolos generada durante el análisis
     * 
     * @return Tabla de símbolos
     */
    public SymbolTable getSymbolTable() {
        return syntacticParser.getSymbolTable();
    }

    /**
     * Obtiene los errores sintácticos encontrados
     * 
     * @return Lista de errores sintácticos
     */
    public List<String> getSyntaxErrors() {
        return syntacticParser.getSyntaxErrors();
    }

    /**
     * Realiza la validación semántica
     * 
     * @return true si no hay errores semánticos, false en caso contrario
     */
    public boolean validateSemantics() {
        // La validación semántica se realiza durante el análisis sintáctico
        // mediante las acciones semánticas definidas en el parser.cup
        // Aquí solo verificamos si hay errores semánticos en la tabla de símbolos
        return getSymbolTable().getSemanticErrors().isEmpty();
    }

    /**
     * Obtiene todos los errores encontrados (sintácticos y semánticos)
     * 
     * @return Lista de todos los errores
     */
    public List<String> getAllErrors() {
        List<String> errors = getSyntaxErrors();
        errors.addAll(getSymbolTable().getSemanticErrors());
        return errors;
    }
}
