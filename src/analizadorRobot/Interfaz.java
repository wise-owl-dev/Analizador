import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;

import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;

/**
 * Interfaz gráfica principal para el analizador de lenguaje de control de
 * robots
 */
public class Interfaz extends JFrame {

    // Componentes de la interfaz
    private JTextPane editorCodigo;
    private JPanel panelNumeroLineas;
    private JTabbedPane panelResultados;
    private JTable tablaTokens;
    private JTextArea areaErrores;
    private JTable tablaSimbolos;
    private JFileChooser selectorArchivos;
    private File archivoActual;
    private JLabel etiquetaEstado;

    // Modelos para las tablas
    private DefaultTableModel modeloTokens;
    private DefaultTableModel modeloSimbolos;

    // Estilos para el editor
    private StyledDocument documentoEditor;
    private Style estiloNormal;
    private Style estiloError;

    /**
     * Constructor
     */
    public Interfaz() {
        // Configuración básica de la ventana
        setTitle("Analizador de Lenguaje de Control de Robots");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar componentes
        inicializarComponentes();

        // Configurar el layout
        configurarLayout();

        // Configurar acciones
        configurarAcciones();
    }

    /**
     * Inicializa los componentes de la interfaz
     */
    private void inicializarComponentes() {
        // Editor de código con estilos
        editorCodigo = new JTextPane();
        documentoEditor = editorCodigo.getStyledDocument();
        editorCodigo.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Panel de números de línea
        panelNumeroLineas = new LineNumberPanel(editorCodigo);

        // Configurar estilos del editor
        estiloNormal = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        estiloError = documentoEditor.addStyle("ErrorStyle", null);
        StyleConstants.setForeground(estiloError, Color.RED);
        StyleConstants.setBold(estiloError, true);

        // Inicializar las tablas
        // Tabla de tokens
        modeloTokens = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Lexema", "Tipo Token", "Línea", "Columna" });
        tablaTokens = new JTable(modeloTokens);

        // Área de errores en lugar de tabla
        areaErrores = new JTextArea();
        areaErrores.setEditable(false);
        areaErrores.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaErrores.setForeground(Color.RED);

        // Tabla de símbolos
        modeloSimbolos = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Nombre", "Tipo", "Valor", "Parámetros", "Rango", "Línea", "Columna" });
        tablaSimbolos = new JTable(modeloSimbolos) {
            @Override
            public Class<?> getColumnClass(int column) {
                // Asegurar que las columnas de parámetros, línea y columna sean tratadas como
                // números
                if (column == 3 || column == 5 || column == 6) {
                    return Integer.class;
                }
                return super.getColumnClass(column);
            }
        };

        // Panel de resultados con pestañas
        panelResultados = new JTabbedPane();
        panelResultados.addTab("Tokens", new JScrollPane(tablaTokens));
        panelResultados.addTab("Errores", new JScrollPane(areaErrores));
        panelResultados.addTab("Tabla de Símbolos", new JScrollPane(tablaSimbolos));

        // Etiqueta de estado
        etiquetaEstado = new JLabel(" ");
        etiquetaEstado.setFont(new Font("Dialog", Font.PLAIN, 12));

        // Selector de archivos
        selectorArchivos = new JFileChooser();
        selectorArchivos.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Archivos de texto (*.txt)";
            }
        });
    }

    /**
     * Configura el layout de la interfaz
     */
    private void configurarLayout() {
        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel para el editor con números de línea
        JPanel panelEditor = new JPanel(new BorderLayout());
        panelEditor.setBorder(BorderFactory.createTitledBorder("Editor de Código"));

        // Agregar el panel de números de línea y el editor
        panelEditor.add(panelNumeroLineas, BorderLayout.WEST);
        panelEditor.add(new JScrollPane(editorCodigo), BorderLayout.CENTER);

        // Crear un JSplitPane para dividir el editor y los resultados
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelEditor,
                panelResultados);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.5);

        // Crear barra de estado
        JPanel barraEstado = new JPanel(new BorderLayout());
        barraEstado.setBorder(BorderFactory.createEtchedBorder());
        barraEstado.add(etiquetaEstado, BorderLayout.WEST);

        // Añadir componentes al panel principal
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        panelPrincipal.add(barraEstado, BorderLayout.SOUTH);

        // Crear menú
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemGuardarComo = new JMenuItem("Guardar Como");
        JMenuItem itemSalir = new JMenuItem("Salir");

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        // Menú Analizar
        JMenu menuAnalizar = new JMenu("Analizar");
        JMenuItem itemAnalizarLexico = new JMenuItem("Análisis Léxico");
        JMenuItem itemAnalizarSintactico = new JMenuItem("Análisis Sintáctico");
        JMenuItem itemAnalizarCompleto = new JMenuItem("Análisis Completo");

        menuAnalizar.add(itemAnalizarLexico);
        menuAnalizar.add(itemAnalizarSintactico);
        menuAnalizar.add(itemAnalizarCompleto);

        // Menú Ejemplos
        JMenu menuEjemplos = new JMenu("Ejemplos");
        JMenuItem itemEjemploCorrecto = new JMenuItem("Cargar Ejemplo Correcto");
        JMenuItem itemEjemploLexico = new JMenuItem("Cargar Ejemplo con Errores Léxicos");
        JMenuItem itemEjemploSintactico = new JMenuItem("Cargar Ejemplo con Errores Sintácticos");

        menuEjemplos.add(itemEjemploCorrecto);
        menuEjemplos.add(itemEjemploLexico);
        menuEjemplos.add(itemEjemploSintactico);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");

        menuAyuda.add(itemAcercaDe);

        // Añadir menus a la barra de menús
        menuBar.add(menuArchivo);
        menuBar.add(menuAnalizar);
        menuBar.add(menuEjemplos);
        menuBar.add(menuAyuda);

        // Configurar acciones de los items del menú
        itemNuevo.addActionListener(e -> nuevoArchivo());
        itemAbrir.addActionListener(e -> abrirArchivo());
        itemGuardar.addActionListener(e -> guardarArchivo());
        itemGuardarComo.addActionListener(e -> guardarArchivoComo());
        itemSalir.addActionListener(e -> System.exit(0));

        itemAnalizarLexico.addActionListener(e -> analizarLexico());
        itemAnalizarSintactico.addActionListener(e -> analizarSintactico());
        itemAnalizarCompleto.addActionListener(e -> analizarCompleto());

        itemEjemploCorrecto.addActionListener(e -> cargarEjemploCorrecto());
        itemEjemploLexico.addActionListener(e -> cargarEjemploErroresLexicos());
        itemEjemploSintactico.addActionListener(e -> cargarEjemploErroresSintacticos());

        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());

        // Establecer la barra de menús
        setJMenuBar(menuBar);

        // Configurar el contenido de la ventana
        setContentPane(panelPrincipal);
    }

    /**
     * Configura las acciones de los botones
     */
    private void configurarAcciones() {
        // Ya no hay botones para configurar, todas las acciones están en el menú

        // Agregar listener para actualizar números de línea cuando el documento cambia
        editorCodigo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                panelNumeroLineas.repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                panelNumeroLineas.repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                panelNumeroLineas.repaint();
            }
        });
    }

    /**
     * Crea un nuevo archivo
     */
    private void nuevoArchivo() {
        if (hayContenidoSinGuardar()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea guardar el contenido actual antes de crear un nuevo archivo?",
                    "Guardar cambios",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                guardarArchivo();
            } else if (opcion == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        editorCodigo.setText("");
        archivoActual = null;
        setTitle("Analizador de Lenguaje de Control de Robots - [Nuevo]");
        limpiarTablas();
    }

    /**
     * Abre un archivo existente
     */
    private void abrirArchivo() {
        if (hayContenidoSinGuardar()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea guardar el contenido actual antes de abrir otro archivo?",
                    "Guardar cambios",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                guardarArchivo();
            } else if (opcion == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        int resultado = selectorArchivos.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = selectorArchivos.getSelectedFile();
            cargarArchivo(archivoActual);
        }
    }

    /**
     * Carga el contenido de un archivo en el editor
     * 
     * @param archivo El archivo a cargar
     */
    private void cargarArchivo(File archivo) {
        try {
            StringBuilder contenido = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String linea;

            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }

            reader.close();

            // Resetear estilos y luego poner el texto
            resetearEstilosEditor();
            try {
                documentoEditor.insertString(0, contenido.toString(), estiloNormal);
            } catch (BadLocationException e) {
                // En caso de error, usar el método setText como respaldo
                editorCodigo.setText(contenido.toString());
            }

            setTitle("Analizador de Lenguaje de Control de Robots - [" + archivo.getName() + "]");
            limpiarTablas();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar el archivo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Guarda el contenido del editor en el archivo actual
     */
    private void guardarArchivo() {
        if (archivoActual == null) {
            guardarArchivoComo();
        } else {
            guardarEnArchivo(archivoActual);
        }
    }

    /**
     * Guarda el contenido del editor en un nuevo archivo
     */
    private void guardarArchivoComo() {
        int resultado = selectorArchivos.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selectorArchivos.getSelectedFile();

            // Asegurar que tenga extensión .txt
            if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                archivo = new File(archivo.getAbsolutePath() + ".txt");
            }

            // Comprobar si existe y confirmar sobrescritura
            if (archivo.exists()) {
                int opcion = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo ya existe. ¿Desea sobrescribirlo?",
                        "Confirmar sobrescritura",
                        JOptionPane.YES_NO_OPTION);

                if (opcion != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            guardarEnArchivo(archivo);
        }
    }

    /**
     * Guarda el contenido del editor en un archivo específico
     * 
     * @param archivo El archivo donde guardar
     */
    private void guardarEnArchivo(File archivo) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));
            writer.write(editorCodigo.getText());
            writer.close();

            archivoActual = archivo;
            setTitle("Analizador de Lenguaje de Control de Robots - [" + archivo.getName() + "]");
            JOptionPane.showMessageDialog(
                    this,
                    "Archivo guardado correctamente.",
                    "Guardado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar el archivo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Realiza el análisis léxico del código actual
     */
    private void analizarLexico() {
        // Limpiar tablas y estilos
        limpiarTablas();

        try {
            // Preparar el analizador léxico
            StringReader reader = new StringReader(editorCodigo.getText());
            Lexer lexer = new Lexer(reader);

            // Lista para almacenar errores
            List<String> erroresLexicos = new ArrayList<>();

            // Escanear todo el código sin procesar los tokens
            // Esto hace que el lexer procese todo el texto y almacene los tokens
            try {
                while (!lexer.yyatEOF()) {
                    java_cup.runtime.Symbol sym = lexer.next_token();
                    // En lugar de verificar sym.sym == sym.ERROR, usa la constante definida en la
                    // clase sym
                    if (sym.sym == 22) { // 22 es el valor de ERROR en la clase sym
                        String errorMsg = "Error léxico en línea " + (sym.left + 1) + ", columna " + (sym.right + 1);
                        if (sym.value != null) {
                            errorMsg += ": " + sym.value;
                        }
                        erroresLexicos.add(errorMsg);

                        // Resaltar el error en el editor
                        resaltarLineaEnEditor(sym.left + 1);
                    }
                }
            } catch (Exception e) {
                // Capturar posibles errores durante el escaneo
                String errorMsg = "Error durante el escaneo léxico: " + e.getMessage();
                erroresLexicos.add(errorMsg);
                System.err.println(errorMsg);
                // Continuamos para mostrar los tokens que sí se pudieron escanear
            }

            // Obtener los tokens encontrados
            ArrayList<Token> tokens = lexer.getTokens();

            // Mostrar tokens en la tabla
            for (Token token : tokens) {
                modeloTokens.addRow(new Object[] {
                        token.getLexema(),
                        token.getTipoToken(),
                        token.getLinea(),
                        token.getColumna()
                });

                // Resaltar los tokens de error en el editor
                if (token.getTipoToken().equals("ERROR")) {
                    resaltarErrorEnEditor(token.getLinea(), token.getColumna(), token.getLexema().length());
                }
            }

            // Mostrar errores en el área de texto
            if (!erroresLexicos.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String error : erroresLexicos) {
                    sb.append(error).append("\n");
                }
                areaErrores.setText(sb.toString());

                // Cambiar a la pestaña de errores si hay errores
                panelResultados.setSelectedIndex(1);
            } else {
                // Cambiar a la pestaña de tokens si no hay errores
                panelResultados.setSelectedIndex(0);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Análisis léxico completado: " + tokens.size() + " tokens encontrados" +
                            (erroresLexicos.isEmpty() ? "." : " con " + erroresLexicos.size() + " errores."),
                    "Análisis Léxico",
                    erroresLexicos.isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error durante el análisis léxico: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Realiza el análisis sintáctico del código actual
     */
    private void analizarSintactico() {
        // Si no hay análisis léxico previo, hacerlo primero
        if (modeloTokens.getRowCount() == 0) {
            // No mostramos los resultados léxicos para no interrumpir
            try {
                // Preparar el analizador léxico
                StringReader reader = new StringReader(editorCodigo.getText());
                Lexer lexer = new Lexer(reader);

                // Escanear todo el código sin procesar los tokens
                while (!lexer.yyatEOF()) {
                    lexer.next_token();
                }
            } catch (Exception e) {
                // Ignorar errores aquí, se manejarán en el análisis sintáctico
            }
        }

        try {
            // Preparar el analizador léxico y sintáctico
            StringReader reader = new StringReader(editorCodigo.getText());
            Lexer lexer = new Lexer(reader);
            parser parser = new parser(lexer);

            // Realizar análisis sintáctico
            parser.parse();

            // Obtener errores y tabla de símbolos
            ArrayList<String> errores = parser.getErrores();
            TablaSimbolo tablaSimbolo = parser.getTablaSimbolo();

            // Añadir errores por robots duplicados
            ArrayList<String> erroresDuplicados = tablaSimbolo.findDuplicateRobots();
            errores.addAll(erroresDuplicados);

            // Mostrar errores en el área de texto y resaltar en el editor
            if (!errores.isEmpty()) {
                // Si ya hay errores léxicos, agregar una separación
                if (!areaErrores.getText().isEmpty()) {
                    areaErrores.append("\n\nERRORES SINTÁCTICOS:\n");
                } else {
                    areaErrores.setText("ERRORES SINTÁCTICOS:\n");
                }

                // Patrón para extraer línea y columna
                Pattern patronLineaColumna = Pattern.compile("línea (\\d+), columna (\\d+)");

                for (String error : errores) {
                    areaErrores.append(error);
                    areaErrores.append("\n");

                    // Extraer línea y columna para resaltar en el editor
                    Matcher matcher = patronLineaColumna.matcher(error);
                    if (matcher.find()) {
                        try {
                            int linea = Integer.parseInt(matcher.group(1));
                            int columna = Integer.parseInt(matcher.group(2));

                            // Resaltar la línea completa donde ocurrió el error
                            resaltarLineaEnEditor(linea);
                        } catch (NumberFormatException e) {
                            // Ignorar si no se puede convertir a número
                        }
                    }
                }

                // Cambiar a la pestaña de errores si hay errores
                panelResultados.setSelectedIndex(1);

                etiquetaEstado
                        .setText("Análisis sintáctico completado. Se encontraron " + errores.size() + " errores.");
            } else {
                // Cambiar a la pestaña de tabla de símbolos si no hay errores
                panelResultados.setSelectedIndex(2);

                etiquetaEstado.setText("Análisis sintáctico completado correctamente.");
            }

            // Mostrar la tabla de símbolos usando el método específico
            mostrarTablaSimbolos(tablaSimbolo);

        } catch (Exception e) {
            areaErrores.append("\nError durante el análisis sintáctico: " + e.getMessage() + "\n");
            panelResultados.setSelectedIndex(1);
            etiquetaEstado.setText("Error durante el análisis sintáctico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia las tablas y el área de resultados
     */
    private void limpiarTablas() {
        while (modeloTokens.getRowCount() > 0) {
            modeloTokens.removeRow(0);
        }

        areaErrores.setText("");

        while (modeloSimbolos.getRowCount() > 0) {
            modeloSimbolos.removeRow(0);
        }

        // Limpiar estilos de error del editor
        resetearEstilosEditor();
    }

    /**
     * Resetea los estilos del editor, eliminando el resaltado de errores
     */
    private void resetearEstilosEditor() {
        try {
            // Guardar el texto
            String textoActual = editorCodigo.getText();
            // Limpiar el documento
            documentoEditor.remove(0, documentoEditor.getLength());
            // Reinsertar el texto con el estilo normal
            documentoEditor.insertString(0, textoActual, estiloNormal);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resalta un error en el editor
     * 
     * @param linea    Línea donde se encuentra el error (comenzando en 1)
     * @param columna  Columna donde se encuentra el error (comenzando en 1)
     * @param longitud Longitud del texto a resaltar
     */
    private void resaltarErrorEnEditor(int linea, int columna, int longitud) {
        try {
            String texto = editorCodigo.getText();
            String[] lineas = texto.split("\n");

            if (linea <= 0 || linea > lineas.length) {
                return; // Línea fuera de rango
            }

            // Ajustar índices a base 0
            linea = linea - 1;
            columna = columna - 1;

            // Calcular la posición absoluta en el documento
            int posicion = 0;
            for (int i = 0; i < linea; i++) {
                posicion += lineas[i].length() + 1; // +1 por el salto de línea
            }

            posicion += columna;

            // Asegurar que no nos pasamos del final del documento
            int longitudMaxima = Math.min(longitud, documentoEditor.getLength() - posicion);
            if (posicion >= 0 && posicion + longitudMaxima <= documentoEditor.getLength()) {
                documentoEditor.setCharacterAttributes(posicion, longitudMaxima, estiloError, true);
            }
        } catch (Exception e) {
            System.err.println("Error al resaltar texto: " + e.getMessage());
        }
    }

    /**
     * Resalta una línea completa en el editor
     * 
     * @param linea Línea a resaltar (comenzando en 1)
     */
    private void resaltarLineaEnEditor(int linea) {
        try {
            String texto = editorCodigo.getText();
            String[] lineas = texto.split("\n");

            if (linea <= 0 || linea > lineas.length) {
                return; // Línea fuera de rango
            }

            // Ajustar índice a base 0
            linea = linea - 1;

            // Calcular la posición absoluta en el documento
            int posicion = 0;
            for (int i = 0; i < linea; i++) {
                posicion += lineas[i].length() + 1; // +1 por el salto de línea
            }

            // Resaltar toda la línea
            int longitud = lineas[linea].length();
            documentoEditor.setCharacterAttributes(posicion, longitud, estiloError, true);
        } catch (Exception e) {
            System.err.println("Error al resaltar línea: " + e.getMessage());
        }
    }

    /**
     * Verifica si hay contenido sin guardar
     * 
     * @return true si hay contenido sin guardar, false en caso contrario
     */
    private boolean hayContenidoSinGuardar() {
        if (archivoActual == null && !editorCodigo.getText().isEmpty()) {
            return true;
        }

        if (archivoActual != null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(archivoActual));
                StringBuilder contenido = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }

                reader.close();
                return !contenido.toString().equals(editorCodigo.getText());
            } catch (IOException e) {
                return true;
            }
        }

        return false;
    }

    /**
     * Carga un ejemplo correcto sin errores
     */
    private void cargarEjemploCorrecto() {
        String ejemplo = "Robot r1\n" +
                "Robot r2\n" +
                "\n" +
                "r1.base = 90\n" +
                "r1.cuerpo = 45\n" +
                "r1.garra = 30\n" +
                "r1.velocidad = 50\n" +
                "\n" +
                "r1.iniciar\n" +
                "r1.base(180)\n" +
                "r1.cuerpo(90)\n" +
                "r1.abrirGarra()\n" +
                "r1.cerrarGarra()\n" +
                "\n" +
                "r1.repetir(5) {\n" +
                "    r1.base(45)\n" +
                "    r1.cuerpo(30)\n" +
                "    r1.garra(15)\n" +
                "}\n" +
                "\n" +
                "r1.detener";

        try {
            // Resetear estilos y luego poner el texto
            resetearEstilosEditor();
            documentoEditor.insertString(0, ejemplo, estiloNormal);

            setTitle("Analizador de Lenguaje de Control de Robots - [Ejemplo Correcto]");
            limpiarTablas();
        } catch (BadLocationException e) {
            // En caso de error, usar el método setText como respaldo
            editorCodigo.setText(ejemplo);
        }
    }

    /**
     * Carga un ejemplo con errores léxicos
     */
    private void cargarEjemploErroresLexicos() {
        String ejemplo = "Robot r1\n" +
                "Robot r2\n" +
                "\n" +
                "r1.base = 90\n" +
                "r1.cuerpo = 45\n" +
                "r1.garra = @30  // Carácter no válido\n" +
                "r1.velocidad = 50.5.5  // Número malformado\n" +
                "\n" +
                "r1.iniciar\n" +
                "r1.base(180)\n" +
                "r1.cuerpo(90)\n" +
                "r1.metodoInexistente()  // Método no reconocido\n" +
                "r1.abrirGarra()\n" +
                "r1.$cerrarGarra()  // Símbolo no válido\n" +
                "\n" +
                "r1.repetir(5) {\n" +
                "    r1.base(#45)  // Carácter no válido\n" +
                "    r1.cuerpo(30)\n" +
                "    r1.garra(15)\n" +
                "}\n" +
                "\n" +
                "r1.detener";

        try {
            // Resetear estilos y luego poner el texto
            resetearEstilosEditor();
            documentoEditor.insertString(0, ejemplo, estiloNormal);

            setTitle("Analizador de Lenguaje de Control de Robots - [Ejemplo con Errores Léxicos]");
            limpiarTablas();
        } catch (BadLocationException e) {
            // En caso de error, usar el método setText como respaldo
            editorCodigo.setText(ejemplo);
        }
    }

    /**
     * Carga un ejemplo con errores sintácticos
     */
    private void cargarEjemploErroresSintacticos() {
        String ejemplo = "Robot r1\n" +
                "Robot r1  // Error: robot duplicado\n" +
                "\n" +
                "r1.base = 500  // Error: valor fuera de rango (0-360)\n" +
                "r1.cuerpo = 45\n" +
                "r1.garra = 100  // Error: valor fuera de rango (0-90)\n" +
                "r1.velocidad = 0  // Error: valor fuera de rango (1-100)\n" +
                "\n" +
                "r3.iniciar  // Error: robot no declarado\n" +
                "r1.iniciar\n" +
                "r1.base(400)  // Error: valor fuera de rango (0-360)\n" +
                "r1.cuerpo(90)\n" +
                "r1.abrirGarra()\n" +
                "r1.cerrarGarra()\n" +
                "\n" +
                "r1.repetir(0) {  // Error: valor inválido (debe ser > 0)\n" +
                "    r1.base(45)\n" +
                "    r1.cuerpo(30)\n" +
                "    r1.garra(15)\n" +
                "}\n" +
                "\n" +
                "r1.detener";

        try {
            // Resetear estilos y luego poner el texto
            resetearEstilosEditor();
            documentoEditor.insertString(0, ejemplo, estiloNormal);

            setTitle("Analizador de Lenguaje de Control de Robots - [Ejemplo con Errores Sintácticos]");
            limpiarTablas();
        } catch (BadLocationException e) {
            // En caso de error, usar el método setText como respaldo
            editorCodigo.setText(ejemplo);
        }
    }

    /**
     * Muestra la tabla de símbolos
     * 
     * @param tablaSimbolo La tabla de símbolos a mostrar
     */
    private void mostrarTablaSimbolos(TablaSimbolo tablaSimbolo) {
        // Limpiar la tabla
        while (modeloSimbolos.getRowCount() > 0) {
            modeloSimbolos.removeRow(0);
        }

        // Primer paso: robots declarados
        for (TablaSimbolo.SimboloInfo simbolo : tablaSimbolo.getSimbolos()) {
            if (simbolo.getTipo().equals("ROBOT")) {
                modeloSimbolos.addRow(new Object[] {
                        simbolo.getNombre(),
                        simbolo.getTipo(),
                        simbolo.getValor() != null ? simbolo.getValor().toString() : "",
                        0, // Sin parámetros para robots
                        "", // Sin rango para robots
                        simbolo.getLinea(),
                        simbolo.getColumna()
                });
            }
        }

        // Mapeo de nombres de métodos correctos
        String[] metodosValidos = {
                "abrirGarra", "base", "cerrarGarra", "cuerpo",
                "detener", "garra", "iniciar", "repetir", "velocidad"
        };

        // Mapa para almacenar los métodos y sus valores
        HashMap<String, String> metodosValores = new HashMap<>();
        // Mapa para almacenar posiciones de métodos
        HashMap<String, int[]> posicionesMetodos = new HashMap<>();
        // Mapa para almacenar si un método tiene parámetros
        HashMap<String, Boolean> metodosConParametros = new HashMap<>();

        // Buscar métodos y sus valores directamente en el código
        procesarCodigoParaMetodos(metodosValidos, metodosValores, posicionesMetodos, metodosConParametros);

        // Agregar métodos encontrados a la tabla
        for (String nombreMetodo : metodosValidos) {
            if (posicionesMetodos.containsKey(nombreMetodo)) {
                int[] posicion = posicionesMetodos.get(nombreMetodo);
                int linea = posicion[0];
                int columna = posicion[1];
                boolean tieneParametros = metodosConParametros.getOrDefault(nombreMetodo, false);
                String valor = metodosValores.getOrDefault(nombreMetodo, "");

                // Determinar el rango según el tipo de método
                String rangoStr = "";
                int numParametros = tieneParametros ? 1 : 0;

                switch (nombreMetodo) {
                    case "base":
                        rangoStr = "[0..360]";
                        break;
                    case "cuerpo":
                        rangoStr = "[0..180]";
                        break;
                    case "garra":
                        rangoStr = "[0..90]";
                        break;
                    case "velocidad":
                        rangoStr = "[1..100]";
                        break;
                    case "repetir":
                        rangoStr = "[1..∞)";
                        break;
                }

                modeloSimbolos.addRow(new Object[] {
                        nombreMetodo,
                        "METODO",
                        valor, // Mostrar el valor si existe
                        numParametros,
                        rangoStr,
                        linea,
                        columna
                });
            }
        }

        // Ajustar el ancho de las columnas
        tablaSimbolos.getColumnModel().getColumn(0).setPreferredWidth(80); // Nombre
        tablaSimbolos.getColumnModel().getColumn(1).setPreferredWidth(70); // Tipo
        tablaSimbolos.getColumnModel().getColumn(2).setPreferredWidth(50); // Valor
        tablaSimbolos.getColumnModel().getColumn(3).setPreferredWidth(80); // Parámetros
        tablaSimbolos.getColumnModel().getColumn(4).setPreferredWidth(80); // Rango
        tablaSimbolos.getColumnModel().getColumn(5).setPreferredWidth(50); // Línea
        tablaSimbolos.getColumnModel().getColumn(6).setPreferredWidth(65); // Columna
    }

    /**
     * Procesa el código para encontrar métodos y sus valores
     */
    private void procesarCodigoParaMetodos(String[] metodosValidos, HashMap<String, String> metodosValores,
            HashMap<String, int[]> posicionesMetodos, HashMap<String, Boolean> metodosConParametros) {

        // Preparar un conjunto de métodos válidos para búsqueda rápida
        HashSet<String> metodosBuscados = new HashSet<>(Arrays.asList(metodosValidos));

        String codigo = editorCodigo.getText();
        String[] lineas = codigo.split("\n");

        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i];

            // Buscar asignaciones: r1.base = 90
            Pattern patternAsign = Pattern.compile("([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)\\s*=\\s*([0-9.]+)");
            Matcher matcherAsign = patternAsign.matcher(linea);

            while (matcherAsign.find()) {
                String metodo = matcherAsign.group(2);
                String valor = matcherAsign.group(3);

                // Solo procesar si es un método válido
                if (metodosBuscados.contains(metodo)) {
                    // Guardar valor
                    metodosValores.put(metodo, valor);

                    // Guardar posición si aún no está registrada
                    if (!posicionesMetodos.containsKey(metodo)) {
                        posicionesMetodos.put(metodo, new int[] { i + 1, matcherAsign.start(2) + 1 });
                    }

                    // Marcar como con parámetros
                    metodosConParametros.put(metodo, true);
                }
            }

            // Buscar llamadas con paréntesis: r1.base(180)
            Pattern patternParam = Pattern.compile("([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)\\s*\\(([^)]+)\\)");
            Matcher matcherParam = patternParam.matcher(linea);

            while (matcherParam.find()) {
                String metodo = matcherParam.group(2);
                String paramStr = matcherParam.group(3).trim();

                // Solo procesar si es un método válido
                if (metodosBuscados.contains(metodo)) {
                    // Si no hay valor previo (de asignación), usar el parámetro como valor
                    if (!metodosValores.containsKey(metodo)) {
                        metodosValores.put(metodo, paramStr);
                    }

                    // Guardar posición si aún no está registrada
                    if (!posicionesMetodos.containsKey(metodo)) {
                        posicionesMetodos.put(metodo, new int[] { i + 1, matcherParam.start(2) + 1 });
                    }

                    // Marcar como con parámetros
                    metodosConParametros.put(metodo, true);
                }
            }

            // Buscar llamadas sin parámetros: r1.iniciar
            Pattern patternNoParam = Pattern.compile("([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)(?!\\s*[=\\(])");
            Matcher matcherNoParam = patternNoParam.matcher(linea);

            while (matcherNoParam.find()) {
                String metodo = matcherNoParam.group(2);

                // Solo procesar si es un método válido y no se ha encontrado antes
                if (metodosBuscados.contains(metodo) && !posicionesMetodos.containsKey(metodo)) {
                    // Guardar posición
                    posicionesMetodos.put(metodo, new int[] { i + 1, matcherNoParam.start(2) + 1 });

                    // Marcar como sin parámetros
                    metodosConParametros.put(metodo, false);
                }
            }

            // Buscar llamadas con paréntesis vacíos: r1.abrirGarra()
            Pattern patternEmpty = Pattern.compile("([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)\\s*\\(\\)");
            Matcher matcherEmpty = patternEmpty.matcher(linea);

            while (matcherEmpty.find()) {
                String metodo = matcherEmpty.group(2);

                // Solo procesar si es un método válido
                if (metodosBuscados.contains(metodo)) {
                    // Guardar posición si aún no está registrada
                    if (!posicionesMetodos.containsKey(metodo)) {
                        posicionesMetodos.put(metodo, new int[] { i + 1, matcherEmpty.start(2) + 1 });
                    }

                    // Marcar como con parámetros (aunque vacíos)
                    metodosConParametros.put(metodo, true);
                }
            }
        }
    }

    /**
     * Componente para mostrar números de línea
     */
    class LineNumberPanel extends JPanel {
        private JTextComponent textComponent;
        private static final int MARGIN = 5;

        public LineNumberPanel(JTextComponent textComponent) {
            this.textComponent = textComponent;
            setPreferredSize(new Dimension(35, 10));
            setBackground(new Color(240, 240, 240));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Configurar gráficos para mejor calidad de texto
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Obtener detalles del componente de texto
            FontMetrics fontMetrics = g.getFontMetrics(textComponent.getFont());
            int lineHeight = fontMetrics.getHeight();
            int ascent = fontMetrics.getAscent();

            // Obtener el texto y calcular el número de líneas
            String text = textComponent.getText();
            int lineCount = text.isEmpty() ? 1 : text.split("\n").length;

            // Determinar la posición visible del viewport
            Rectangle visibleRect = textComponent.getVisibleRect();
            int startOffset = textComponent.viewToModel2D(new Point(visibleRect.x, visibleRect.y));
            int endOffset = textComponent.viewToModel2D(new Point(visibleRect.x, visibleRect.y + visibleRect.height));

            // Calcular la línea inicial y final visible
            int startLine = 0;
            int endLine = lineCount - 1;

            try {
                startLine = textComponent.getDocument().getDefaultRootElement().getElementIndex(startOffset);
                endLine = textComponent.getDocument().getDefaultRootElement().getElementIndex(endOffset);
            } catch (Exception e) {
                // Ignorar errores y usar valores predeterminados
            }

            // Dibujar los números de línea
            for (int i = startLine; i <= endLine; i++) {
                try {
                    // Obtener la posición Y del inicio de la línea
                    Rectangle r = textComponent.modelToView2D(
                            textComponent.getDocument().getDefaultRootElement().getElement(i).getStartOffset())
                            .getBounds();

                    // Dibujar el número de línea
                    g2d.setColor(new Color(80, 80, 80));
                    g2d.drawString(String.valueOf(i + 1), MARGIN, r.y + ascent);
                } catch (Exception e) {
                    // Ignorar errores
                }
            }
        }
    }

    /**
     * Realiza el análisis léxico y sintáctico completo
     */
    private void analizarCompleto() {
        // Primero realiza el análisis léxico
        analizarLexico();

        // Luego realiza el análisis sintáctico
        analizarSintactico();
    }

    /**
     * Muestra la ventana "Acerca de"
     */
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(
                this,
                "Analizador de Lenguaje de Control de Robots\n" +
                        "Versión 1.0\n\n" +
                        "Una aplicación para analizar programas de control de brazos robóticos",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Método principal para iniciar la aplicación
     */
    public static void main(String[] args) {
        try {
            // Configurar el Look and Feel para que se vea mejor
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Interfaz app = new Interfaz();
            app.setVisible(true);
        });
    }
}