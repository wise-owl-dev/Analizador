package main.java.robotcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que implementa una tabla de símbolos para el análisis semántico
 */
public class TablaSimbolo {
    // Mapa para almacenar símbolos por nombre
    private Map<String, Simbolo> simbolos;
    // Lista para almacenar errores semánticos
    private List<String> semanticErrors;

    /**
     * Constructor que inicializa la tabla de símbolos y los métodos predefinidos
     */
    public TablaSimbolo() {
        simbolos = new HashMap<>();
        semanticErrors = new ArrayList<>();

        // Agregar métodos predefinidos con sus rangos
        addPredefinedMethods();
    }

    /**
     * Agrega los métodos predefinidos a la tabla de símbolos
     */
    private void addPredefinedMethods() {
        // Métodos con parámetros y sus rangos
        simbolos.put("base", new Simbolo("base", "METODO", 0, 0, 360));
        simbolos.put("cuerpo", new Simbolo("cuerpo", "METODO", 0, 0, 180));
        simbolos.put("garra", new Simbolo("garra", "METODO", 0, 0, 90));
        simbolos.put("velocidad", new Simbolo("velocidad", "METODO", 0, 1, 100));
        simbolos.put("repetir", new Simbolo("repetir", "METODO", 0, 1, Integer.MAX_VALUE));

        // Métodos sin parámetros
        simbolos.put("abrirGarra", new Simbolo("abrirGarra", "METODO", 0, 0, 0));
        simbolos.put("cerrarGarra", new Simbolo("cerrarGarra", "METODO", 0, 0, 0));
        simbolos.put("iniciar", new Simbolo("iniciar", "METODO", 0, 0, 0));
        simbolos.put("detener", new Simbolo("detener", "METODO", 0, 0, 0));
    }

    /**
     * Agrega un símbolo a la tabla
     * 
     * @param Simbolo Símbolo a agregar
     * @return true si se agregó correctamente, false si ya existía
     */
    public boolean addSimbolo(Simbolo Simbolo) {
        if (simbolos.containsKey(Simbolo.getName()) && Simbolo.getType().equals("ROBOT")) {
            semanticErrors.add("Error semántico: Robot '" + Simbolo.getName() + "' ya definido");
            return false;
        }

        simbolos.put(Simbolo.getName(), Simbolo);
        return true;
    }

    /**
     * Verifica si un símbolo existe en la tabla
     * 
     * @param name Nombre del símbolo
     * @return true si existe, false en caso contrario
     */
    public boolean containsSimbolo(String name) {
        return simbolos.containsKey(name);
    }

    /**
     * Obtiene un símbolo de la tabla
     * 
     * @param name Nombre del símbolo
     * @return Símbolo o null si no existe
     */
    public Simbolo getSimbolo(String name) {
        return simbolos.get(name);
    }

    /**
     * Valida si un valor está dentro del rango permitido para un método
     * 
     * @param method Nombre del método
     * @param value  Valor a validar
     * @return true si está en rango, false en caso contrario
     */
    public boolean validateMethodRange(String method, int value) {
        if (!simbolos.containsKey(method)) {
            semanticErrors.add("Error semántico: Método '" + method + "' no definido");
            return false;
        }

        Simbolo Simbolo = simbolos.get(method);
        boolean inRange = Simbolo.isInRange(value);

        if (!inRange) {
            semanticErrors.add("Error semántico: Valor " + value + " fuera de rango para método '"
                    + method + "' (rango: " + Simbolo.getMinValue() + "-" + Simbolo.getMaxValue() + ")");
        }

        return inRange;
    }

    /**
     * Obtiene todos los errores semánticos encontrados
     * 
     * @return Lista de errores semánticos
     */
    public List<String> getSemanticErrors() {
        return semanticErrors;
    }

    /**
     * Agrega un error semántico a la lista
     * 
     * @param error Mensaje de error
     */
    public void addSemanticError(String error) {
        semanticErrors.add(error);
    }

    /**
     * Obtiene una representación de texto de la tabla de símbolos
     * 
     * @return Representación de texto
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tabla de Símbolos:\n");

        for (Simbolo Simbolo : simbolos.values()) {
            result.append(Simbolo.toString()).append("\n");
        }

        return result.toString();
    }

    /**
     * Obtiene todos los símbolos en la tabla
     * 
     * @return Lista de símbolos
     */
    public List<Simbolo> getAllSimbolos() {
        return new ArrayList<>(simbolos.values());
    }
}
