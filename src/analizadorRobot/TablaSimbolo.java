package analizadorRobot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase que implementa una tabla de símbolos mejorada para el lenguaje de
 * control de robots
 */
public class TablaSimbolo {

    /**
     * Clase interna para almacenar información de un símbolo
     */
    public class SymbolInfo {
        private String name; // Nombre del símbolo
        private String type; // Tipo del símbolo (ROBOT, METHOD, etc.)
        private Object value; // Valor del símbolo (si aplica)
        private int numParams; // Número de parámetros (para métodos)
        private int minValue; // Valor mínimo permitido
        private int maxValue; // Valor máximo permitido
        private int line; // Línea donde se definió
        private int column; // Columna donde se definió

        public SymbolInfo(String name, String type) {
            this.name = name;
            this.type = type;
            this.value = null;
            this.numParams = 0;
            this.minValue = 0;
            this.maxValue = 0;
            this.line = 0;
            this.column = 0;
        }

        public SymbolInfo(String name, String type, Object value, int numParams, int minValue, int maxValue) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.numParams = numParams;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.line = 0;
            this.column = 0;
        }

        public SymbolInfo(String name, String type, Object value, int numParams, int minValue, int maxValue, int line,
                int column) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.numParams = numParams;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.line = line;
            this.column = column;
        }

        // Getters
        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public int getNumParams() {
            return numParams;
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        // Setters
        public void setValue(Object value) {
            this.value = value;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        @Override
        public String toString() {
            return "SymbolInfo[name=" + name + ", type=" + type + ", value=" + value +
                    ", params=" + numParams + ", range=[" + minValue + ".." + maxValue + "], " +
                    "pos=(" + line + "," + column + ")]";
        }
    }

    // Estructura de datos para almacenar los símbolos
    private HashMap<String, SymbolInfo> symbols;
    private HashMap<String, SymbolInfo> methods;

    /**
     * Constructor de la tabla de símbolos
     */
    public TablaSimbolo() {
        symbols = new HashMap<>();
        methods = new HashMap<>();

        // Añadir métodos predefinidos con sus rangos
        addMethod("base", 1, 0, 360);
        addMethod("cuerpo", 1, 0, 180);
        addMethod("garra", 1, 0, 90);
        addMethod("velocidad", 1, 1, 100);
        addMethod("abrirGarra", 0, 0, 0);
        addMethod("cerrarGarra", 0, 0, 0);
        addMethod("repetir", 1, 1, Integer.MAX_VALUE);
        addMethod("iniciar", 0, 0, 0);
        addMethod("detener", 0, 0, 0);
    }

    /**
     * Añade un símbolo a la tabla
     * 
     * @param name Nombre del símbolo
     * @param type Tipo del símbolo
     * @return true si se añadió correctamente, false si ya existía
     */
    public boolean addSymbol(String name, String type) {
        if (!symbols.containsKey(name)) {
            symbols.put(name, new SymbolInfo(name, type));
            return true;
        }
        return false;
    }

    /**
     * Añade un símbolo a la tabla con información de posición
     * 
     * @param name   Nombre del símbolo
     * @param type   Tipo del símbolo
     * @param line   Línea donde se definió
     * @param column Columna donde se definió
     * @return true si se añadió correctamente, false si ya existía
     */
    public boolean addSymbol(String name, String type, int line, int column) {
        if (!symbols.containsKey(name)) {
            SymbolInfo info = new SymbolInfo(name, type);
            info.setLine(line);
            info.setColumn(column);
            symbols.put(name, info);
            return true;
        }
        return false;
    }

    /**
     * Añade un método a la tabla con sus rangos de valores
     * 
     * @param name      Nombre del método
     * @param numParams Número de parámetros
     * @param minValue  Valor mínimo permitido
     * @param maxValue  Valor máximo permitido
     */
    private void addMethod(String name, int numParams, int minValue, int maxValue) {
        methods.put(name, new SymbolInfo(name, "METHOD", null, numParams, minValue, maxValue));
    }

    /**
     * Verifica si existe un símbolo en la tabla
     * 
     * @param name Nombre del símbolo
     * @return true si existe, false si no
     */
    public boolean symbolExists(String name) {
        return symbols.containsKey(name);
    }

    /**
     * Obtiene la información de un símbolo
     * 
     * @param name Nombre del símbolo
     * @return Información del símbolo o null si no existe
     */
    public SymbolInfo getSymbolInfo(String name) {
        return symbols.get(name);
    }

    /**
     * Obtiene la información de un método
     * 
     * @param name Nombre del método
     * @return Información del método o null si no existe
     */
    public SymbolInfo getMethodInfo(String name) {
        return methods.get(name);
    }

    /**
     * Obtiene todos los símbolos de la tabla
     * 
     * @return Lista con todos los símbolos
     */
    public ArrayList<SymbolInfo> getAllSymbols() {
        ArrayList<SymbolInfo> result = new ArrayList<>(symbols.values());
        return result;
    }

    /**
     * Obtiene todos los métodos predefinidos
     * 
     * @return Lista con todos los métodos
     */
    public ArrayList<SymbolInfo> getAllMethods() {
        ArrayList<SymbolInfo> result = new ArrayList<>(methods.values());
        return result;
    }

    /**
     * Busca robots duplicados en la tabla de símbolos
     * 
     * @return Lista de errores encontrados
     */
    public ArrayList<String> findDuplicateRobots() {
        ArrayList<String> errors = new ArrayList<>();
        HashMap<String, SymbolInfo> robotsFound = new HashMap<>();

        for (SymbolInfo symbol : symbols.values()) {
            if (symbol.getType().equals("ROBOT")) {
                if (robotsFound.containsKey(symbol.getName())) {
                    SymbolInfo first = robotsFound.get(symbol.getName());
                    errors.add("Error semántico en línea " + symbol.getLine() + ", columna " + symbol.getColumn() +
                            ": Robot '" + symbol.getName() + "' ya declarado previamente en línea " +
                            first.getLine() + ", columna " + first.getColumn());
                } else {
                    robotsFound.put(symbol.getName(), symbol);
                }
            }
        }

        return errors;
    }

    /**
     * Imprime el contenido de la tabla de símbolos
     */
    public void printSymbolTable() {
        System.out.println("===== TABLA DE SÍMBOLOS =====");
        System.out.println("--- Robots ---");
        for (SymbolInfo symbol : symbols.values()) {
            if (symbol.getType().equals("ROBOT")) {
                System.out.println(symbol);
            }
        }

        System.out.println("--- Métodos predefinidos ---");
        for (SymbolInfo method : methods.values()) {
            System.out.println(method);
        }
        System.out.println("============================");
    }
}