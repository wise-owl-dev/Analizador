package main.java.robotcontrol;

/**
 * Clase que representa un símbolo en la tabla de símbolos
 */
public class Simbolo {
    private String name; // Nombre del símbolo
    private String type; // Tipo del símbolo (ROBOT, MÉTODO, etc.)
    private int value; // Valor asociado (para números)
    private int minValue; // Valor mínimo permitido
    private int maxValue; // Valor máximo permitido
    private int line; // Línea donde se declaró
    private int column; // Columna donde se declaró

    /**
     * Constructor para símbolos que no necesitan rangos
     * 
     * @param name   Nombre del símbolo
     * @param type   Tipo del símbolo
     * @param line   Línea donde se declaró
     * @param column Columna donde se declaró
     */
    public Simbolo(String name, String type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = 0;
        this.minValue = 0;
        this.maxValue = 0;
    }

    /**
     * Constructor para símbolos con valor y rango
     * 
     * @param name     Nombre del símbolo
     * @param type     Tipo del símbolo
     * @param value    Valor asociado
     * @param minValue Valor mínimo permitido
     * @param maxValue Valor máximo permitido
     */
    public Simbolo(String name, String type, int value, int minValue, int maxValue) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.line = 0;
        this.column = 0;
    }

    /**
     * Constructor para símbolos con valor y rango, incluyendo posición
     * 
     * @param name     Nombre del símbolo
     * @param type     Tipo del símbolo
     * @param value    Valor asociado
     * @param minValue Valor mínimo permitido
     * @param maxValue Valor máximo permitido
     * @param line     Línea donde se declaró
     * @param column   Columna donde se declaró
     */
    public Simbolo(String name, String type, int value, int minValue, int maxValue, int line, int column) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.line = line;
        this.column = column;
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Verifica si un valor está dentro del rango permitido
     * 
     * @param value Valor a verificar
     * @return true si está en rango, false en caso contrario
     */
    public boolean isInRange(int value) {
        return value >= minValue && value <= maxValue;
    }

    @Override
    public String toString() {
        if (type.equals("ROBOT") || type.equals("IDENTIFICADOR")) {
            return String.format("Simbolo[name=%s, type=%s, line=%d, column=%d]", name, type, line, column);
        } else {
            return String.format("Simbolo[name=%s, type=%s, value=%d, range=%d-%d, line=%d, column=%d]",
                    name, type, value, minValue, maxValue, line, column);
        }
    }
}
