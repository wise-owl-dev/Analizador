package analizadorRobot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import analizadorRobot.*;
import analizadorRobot.jcup.parser;
import analizadorRobot.jflex.Lexico;

/**
 * Clase principal que implementa la interfaz gráfica y controla la ejecución
 * del análisis
 */
public class Main extends JFrame {
    // Componentes de la GUI
    private JTextArea codeTextArea;
    private JTable tokensTable;
    private DefaultTableModel tokensTableModel;
    private JTable symbolsTable;
    private DefaultTableModel symbolsTableModel;
    private JButton analyzeButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton exampleButton;
    private JLabel statusLabel;

    /**
     * Constructor de la interfaz gráfica
     */
    public Main() {
        // Configuración básica de la ventana
        super("Analizador de Lenguaje Robot");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel superior para botones y título
        JPanel topPanel = new JPanel(new BorderLayout());

        // Panel para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        exampleButton = new JButton("Cargar Ejemplo");
        exampleButton.addActionListener(e -> loadExampleCode());

        analyzeButton = new JButton("Analizar");
        analyzeButton.addActionListener(e -> analyzeCode());

        clearButton = new JButton("Limpiar");
        clearButton.addActionListener(e -> clearAll());

        saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> saveCode());

        loadButton = new JButton("Cargar");
        loadButton.addActionListener(e -> loadCode());

        buttonPanel.add(exampleButton);
        buttonPanel.add(analyzeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        // Etiqueta para el título
        JLabel titleLabel = new JLabel("Analizador de Lenguaje Robot", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Panel central dividido en dos partes
        JSplitPane centralPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centralPanel.setDividerLocation(450);

        // Panel izquierdo para el código fuente
        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel codeLabel = new JLabel("Editor de Código", JLabel.CENTER);
        codeTextArea = new JTextArea();
        codeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Agregar numeración de líneas al JTextArea
        JScrollPane codeScroll = new JScrollPane(codeTextArea);
        codeScroll.setRowHeaderView(new LineNumberView(codeTextArea));

        leftPanel.add(codeLabel, BorderLayout.NORTH);
        leftPanel.add(codeScroll, BorderLayout.CENTER);

        // Panel derecho dividido en dos partes verticalmente
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Tabla de símbolos
        JLabel symbolsLabel = new JLabel("Tabla de Símbolos", JLabel.CENTER);
        String[] symbolsColumns = { "NOMBRE", "TIPO", "VALOR" };
        symbolsTableModel = new DefaultTableModel(symbolsColumns, 0);
        symbolsTable = new JTable(symbolsTableModel);
        JScrollPane symbolsScroll = new JScrollPane(symbolsTable);

        // Tabla de tokens
        JLabel tokensLabel = new JLabel("Tokens", JLabel.CENTER);
        String[] tokensColumns = { "TOKEN", "TIPO", "VALOR", "LÍNEA", "COLUMNA" };
        tokensTableModel = new DefaultTableModel(tokensColumns, 0);
        tokensTable = new JTable(tokensTableModel);
        JScrollPane tokensScroll = new JScrollPane(tokensTable);

        // Dividir el panel derecho en dos
        JSplitPane rightSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel symbolsPanel = new JPanel(new BorderLayout());
        symbolsPanel.add(symbolsLabel, BorderLayout.NORTH);
        symbolsPanel.add(symbolsScroll, BorderLayout.CENTER);

        JPanel tokensPanel = new JPanel(new BorderLayout());
        tokensPanel.add(tokensLabel, BorderLayout.NORTH);
        tokensPanel.add(tokensScroll, BorderLayout.CENTER);

        rightSplitPanel.setTopComponent(symbolsPanel);
        rightSplitPanel.setBottomComponent(tokensPanel);
        rightSplitPanel.setDividerLocation(200);

        rightPanel.add(rightSplitPanel, BorderLayout.CENTER);

        // Agregar los paneles al panel central
        centralPanel.setLeftComponent(leftPanel);
        centralPanel.setRightComponent(rightPanel);

        // Panel inferior para mensajes de estado
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Listo para analizar código");
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        // Agregar todos los paneles al panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centralPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Agregar el panel principal al frame
        setContentPane(mainPanel);

        // Establecer un aspecto y sentido moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cargar código de ejemplo
        loadExampleCode();
    }

    /**
     * Carga código de ejemplo en el área de texto
     */
    private void loadExampleCode() {
        String exampleCode = "Robot r1\n" +
                "Robot r2\n\n" +
                "r1.iniciar\n" +
                "r1.velocidad=50\n" +
                "r1.base=180\n" +
                "r1.cuerpo=45\n" +
                "r1.garra=90\n\n" +
                "r1.abrirGarra()\n" +
                "r1.repetir(3) {\n" +
                "    r1.base(360)\n" +
                "    r1.cuerpo(90)\n" +
                "    r1.garra(45)\n" +
                "}\n" +
                "r1.cerrarGarra()\n" +
                "r1.detener\n\n" +
                "r2.iniciar\n" +
                "r2.velocidad=30\n" +
                "r2.base=90\n" +
                "r2.detener";

        codeTextArea.setText(exampleCode);
        statusLabel.setText("Código de ejemplo cargado. Presione 'Analizar' para procesar.");
    }

    /**
     * Ejecuta el análisis léxico y sintáctico del código
     */
    private void analyzeCode() {
        try {
            String code = codeTextArea.getText();

            if (code.trim().isEmpty()) {
                statusLabel.setText("Error: No hay código para analizar");
                return;
            }

            // Limpiar las tablas
            clearTables();

            // Análisis léxico
            Lexico lexer = new Lexico(new StringReader(code));

            // Iniciar análisis sintáctico
            parser p = new parser(lexer);

            // Ejecutar análisis
            p.parse();

            // Obtener resultados
            ArrayList<Token> tokens = lexer.getTokens();
            TablaSimbolo symbolTable = p.getSymbolTable();

            // Mostrar tokens en la tabla
            displayTokens(tokens);

            // Mostrar tabla de símbolos
            displaySymbolTable(symbolTable);

            statusLabel.setText("Análisis completado con éxito");

        } catch (Exception e) {
            statusLabel.setText("Error durante el análisis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra los tokens en la tabla
     */
    private void displayTokens(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            tokensTableModel.addRow(new Object[] {
                    token.getLexeme(),
                    token.getTokenType(),
                    token.getLexeme(),
                    token.getLine(),
                    token.getColumn()
            });
        }
    }

    /**
     * Muestra la tabla de símbolos
     */
    private void displaySymbolTable(TablaSimbolo symbolTable) {
        // Mostrar robots
        for (TablaSimbolo.SymbolInfo symbol : symbolTable.getAllSymbols()) {
            if (symbol.getType().equals("ROBOT")) {
                symbolsTableModel.addRow(new Object[] {
                        symbol.getName(),
                        "Robot",
                        ""
                });
            }
        }

        // Mostrar métodos predefinidos
        for (TablaSimbolo.SymbolInfo method : symbolTable.getAllMethods()) {
            symbolsTableModel.addRow(new Object[] {
                    method.getName(),
                    "Método",
                    "[" + method.getMinValue() + ".." + method.getMaxValue() + "]"
            });
        }
    }

    /**
     * Guarda el código actual en un archivo
     */
    private void saveCode() {
        String code = codeTextArea.getText();
        if (code.trim().isEmpty()) {
            statusLabel.setText("Error: No hay código para guardar");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Código");
        fileChooser.setSelectedFile(new File("codigo.rob"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Robot (*.rob)", "rob"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Añadir extensión .rob si no la tiene
            if (!filePath.toLowerCase().endsWith(".rob")) {
                filePath += ".rob";
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(code);
                statusLabel.setText("Código guardado en: " + filePath);
            } catch (IOException ex) {
                statusLabel.setText("Error al guardar el archivo: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Carga código desde un archivo
     */
    private void loadCode() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar Código");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Robot (*.rob)", "rob"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                codeTextArea.setText(content.toString());
                statusLabel.setText("Archivo cargado exitosamente");
            } catch (IOException e) {
                statusLabel.setText("Error al cargar archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Limpiar todas las tablas y campos
     */
    private void clearAll() {
        codeTextArea.setText("");
        clearTables();
        statusLabel.setText("Todo limpiado");
    }

    /**
     * Limpiar solo las tablas
     */
    private void clearTables() {
        tokensTableModel.setRowCount(0);
        symbolsTableModel.setRowCount(0);
    }

    /**
     * Componente para mostrar números de línea en un JTextArea
     */
    private class LineNumberView extends JComponent {
        private final JTextComponent textComponent;
        private final FontMetrics fontMetrics;
        private final int digitWidth;
        private final int insets = 5;

        public LineNumberView(JTextComponent textComponent) {
            this.textComponent = textComponent;
            setFont(new Font("monospaced", Font.PLAIN, textComponent.getFont().getSize()));
            fontMetrics = getFontMetrics(getFont());
            digitWidth = fontMetrics.stringWidth("9");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Rectangle clip = g.getClipBounds();
            int startOffset = textComponent.viewToModel(new Point(0, clip.y));
            int endOffset = textComponent.viewToModel(new Point(0, clip.y + clip.height));

            while (startOffset <= endOffset) {
                try {
                    int lineNumber = textComponent.getDocument().getDefaultRootElement().getElementIndex(startOffset)
                            + 1;
                    String lineNumberStr = String.valueOf(lineNumber);

                    int y = textComponent.modelToView(startOffset).y + fontMetrics.getAscent();
                    int strWidth = fontMetrics.stringWidth(lineNumberStr);

                    g.setColor(Color.GRAY);
                    g.drawString(lineNumberStr, getWidth() - strWidth - insets, y);

                    startOffset = javax.swing.text.Utilities.getRowEnd(textComponent, startOffset) + 1;
                } catch (BadLocationException e) {
                    break;
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            int lines = textComponent.getDocument().getDefaultRootElement().getElementCount();
            int digits = Math.max(2, String.valueOf(lines).length());
            return new Dimension(digits * digitWidth + 2 * insets, textComponent.getHeight());
        }
    }

    /**
     * Método principal para iniciar la aplicación
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        try {
            // Establecer look and feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}