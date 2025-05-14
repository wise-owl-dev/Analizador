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
    public class SimboloInfo {
        private String nombre; // Nombre del símbolo
        private String tipo; // Tipo del símbolo (ROBOT, METHOD, etc.)
        private Object valor; // Valor del símbolo (si aplica)
        private int numParametros; // Número de parámetros (para métodos)
        private int minValor; // Valor mínimo permitido
        private int maxValor; // Valor máximo permitido
        private int linea; // Línea donde se definió
        private int columna; // Columna donde se definió

        public SimboloInfo(String nombre, String tipo) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.valor = null;
            this.numParametros = 0;
            this.minValor = 0;
            this.maxValor = 0;
            this.linea = 0;
            this.columna = 0;
        }

        public SimboloInfo(String nombre, String tipo, Object valor, int numParametros, int minValor, int maxValor) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.valor = valor;
            this.numParametros = numParametros;
            this.minValor = minValor;
            this.maxValor = maxValor;
            this.linea = 0;
            this.columna = 0;
        }

        public SimboloInfo(String nombre, String tipo, Object valor, int numParametros, int minValor, int maxValor,
                int linea, int columna) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.valor = valor;
            this.numParametros = numParametros;
            this.minValor = minValor;
            this.maxValor = maxValor;
            this.linea = linea;
            this.columna = columna;
        }

        // Getters
        public String getNombre() {
            return nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public Object getValor() {
            return valor;
        }

        public int getNumParametros() {
            return numParametros;
        }

        public int getMinValor() {
            return minValor;
        }

        public int getMaxValor() {
            return maxValor;
        }

        public int getLinea() {
            return linea;
        }

        public int getColumna() {
            return columna;
        }

        // Setters
        public void setValor(Object value) {
            this.valor = value;
        }

        public void setLinea(int line) {
            this.linea = line;
        }

        public void setColumna(int column) {
            this.columna = column;
        }

        @Override
        public String toString() {
            return "SymbolInfo[name=" + nombre + ", type=" + tipo + ", value=" + valor +
                    ", params=" + numParametros + ", range=[" + minValor + ".." + maxValor + "], " +
                    "pos=(" + linea + "," + columna + ")]";
        }
    }

    // Estructura de datos para almacenar los símbolos
    private HashMap<String, SimboloInfo> simbolos;
    private HashMap<String, SimboloInfo> metodos;

    /**
     * Constructor de la tabla de símbolos
     */
    public TablaSimbolo() {
        simbolos = new HashMap<>();
        metodos = new HashMap<>();

        // Añadir métodos predefinidos con sus rangos
        agregarMetodo("base", 1, 0, 360);
        agregarMetodo("cuerpo", 1, 0, 180);
        agregarMetodo("garra", 1, 0, 90);
        agregarMetodo("velocidad", 1, 1, 100);
        agregarMetodo("abrirGarra", 0, 0, 0);
        agregarMetodo("cerrarGarra", 0, 0, 0);
        agregarMetodo("repetir", 1, 1, Integer.MAX_VALUE);
        agregarMetodo("iniciar", 0, 0, 0);
        agregarMetodo("detener", 0, 0, 0);
    }

    /**
     * Añade un símbolo a la tabla
     * 
     * @param nombre Nombre del símbolo
     * @param tipo   Tipo del símbolo
     * @return true si se añadió correctamente, false si ya existía
     */
    public boolean agregarSimbolo(String nombre, String tipo) {
        if (!simbolos.containsKey(nombre)) {
            simbolos.put(nombre, new SimboloInfo(nombre, tipo));
            return true;
        }
        return false;
    }

    /**
     * Añade un símbolo a la tabla con información de posición
     * 
     * @param nombre  Nombre del símbolo
     * @param tipo    Tipo del símbolo
     * @param linea   Línea donde se definió
     * @param columna Columna donde se definió
     * @return true si se añadió correctamente, false si ya existía
     */
    public boolean agregarSimbolo(String nombre, String tipo, int linea, int columna) {
        if (!simbolos.containsKey(nombre)) {
            SimboloInfo info = new SimboloInfo(nombre, tipo);
            info.setLinea(linea + 1); // Ajustando a base 1 para mostrar líneas desde 1, no 0
            info.setColumna(columna + 1); // Ajustando a base 1
            simbolos.put(nombre, info);
            return true;
        }
        return false;
    }

    /**
     * Añade un método a la tabla con sus rangos de valores
     * 
     * @param nombre        Nombre del método
     * @param numParametros Número de parámetros
     * @param minValor      Valor mínimo permitido
     * @param maxValor      Valor máximo permitido
     */
    private void agregarMetodo(String nombre, int numParametros, int minValor, int maxValor) {
        metodos.put(nombre, new SimboloInfo(nombre, "METODO", null, numParametros, minValor, maxValor));
    }

    /**
     * Verifica si existe un símbolo en la tabla
     * 
     * @param nombre Nombre del símbolo
     * @return true si existe, false si no
     */
    public boolean simboloExiste(String nombre) {
        return simbolos.containsKey(nombre);
    }

    /**
     * Obtiene la información de un símbolo
     * 
     * @param nombre Nombre del símbolo
     * @return Información del símbolo o null si no existe
     */
    public SimboloInfo getSimboloInfo(String nombre) {
        return simbolos.get(nombre);
    }

    /**
     * Obtiene la información de un método
     * 
     * @param nombre Nombre del método
     * @return Información del método o null si no existe
     */
    public SimboloInfo getMetodoInfo(String nombre) {
        return metodos.get(nombre);
    }

    /**
     * Obtiene todos los símbolos de la tabla
     * 
     * @return Lista con todos los símbolos
     */
    public ArrayList<SimboloInfo> getSimbolos() {
        ArrayList<SimboloInfo> resultado = new ArrayList<>(simbolos.values());
        return resultado;
    }

    /**
     * Obtiene todos los métodos predefinidos
     * 
     * @return Lista con todos los métodos
     */
    public ArrayList<SimboloInfo> getMetodos() {
        ArrayList<SimboloInfo> resultado = new ArrayList<>(metodos.values());
        return resultado;
    }

    /**
     * Busca robots duplicados en la tabla de símbolos
     * 
     * @return Lista de errores encontrados
     */
    public ArrayList<String> findDuplicateRobots() {
        ArrayList<String> errores = new ArrayList<>();
        HashMap<String, SimboloInfo> robotsEncontrados = new HashMap<>();

        for (SimboloInfo simbolo : simbolos.values()) {
            if (simbolo.getTipo().equals("ROBOT")) {
                if (robotsEncontrados.containsKey(simbolo.getNombre())) {
                    SimboloInfo first = robotsEncontrados.get(simbolo.getNombre());
                    errores.add("Error semántico en línea " + simbolo.getLinea() + ", columna " + simbolo.getColumna() +
                            ": Robot '" + simbolo.getNombre() + "' ya declarado previamente en línea " +
                            first.getLinea() + ", columna " + first.getColumna());
                } else {
                    robotsEncontrados.put(simbolo.getNombre(), simbolo);
                }
            }
        }

        return errores;
    }

    /**
     * Imprime el contenido de la tabla de símbolos
     */
    public void imprimirTablaSimbolo() {
        System.out.println("===== TABLA DE SÍMBOLOS =====");
        System.out.println("--- Robots ---");
        for (SimboloInfo simbolo : simbolos.values()) {
            if (simbolo.getTipo().equals("ROBOT")) {
                System.out.println(simbolo);
            }
        }

        System.out.println("--- Métodos predefinidos ---");
        for (SimboloInfo metodo : metodos.values()) {
            System.out.println(metodo);
        }
        System.out.println("============================");
    }
}