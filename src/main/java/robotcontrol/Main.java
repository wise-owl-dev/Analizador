package main.java.robotcontrol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Clase principal para ejecutar el analizador léxico, sintáctico y semántico
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Usar un cuadro de diálogo para elegir un archivo de código
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto", "txt");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "No se seleccionó ningún archivo", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Leer el archivo seleccionado
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            StringBuilder sourceCode = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sourceCode.append(line).append("\n");
                }
            }

            // Mostrar el código fuente
            System.out.println("Código fuente a analizar:");
            System.out.println("------------------------");
            System.out.println(sourceCode.toString());
            System.out.println("------------------------\n");

            // Realizar análisis léxico
            System.out.println("Realizando análisis léxico...");
            AnalizadorSintactico lexer = AnalizadorSintactico(new StringReader(sourceCode.toString()));

            while (true) {
                Token token = lexer.yylex();
                if (token == null)
                    break;
                if (token == Token.ERROR) {
                    System.out.println(
                            "Error léxico detectado: " + lexer.getLexemes().get(lexer.getLexemes().size() - 1));
                }
            }

            // Mostrar tokens encontrados
            System.out.println("\nTokens encontrados:");
            System.out.println("------------------");
            System.out.println(lexer.getTokensText());

            // Verificar si hay errores léxicos
            boolean hasLexicalErrors = lexer.getTokens().contains(Token.ERROR);
            if (hasLexicalErrors) {
                System.out.println("\nSe encontraron errores léxicos. Corrija el código antes de continuar.");
                JOptionPane.showMessageDialog(null,
                        "Se encontraron errores léxicos. Revise la consola para más detalles.",
                        "Errores Léxicos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Realizar análisis sintáctico y semántico
            System.out.println("\nRealizando análisis sintáctico y semántico...");
            SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(sourceCode.toString());
            boolean syntaxSuccess = syntaxAnalyzer.analyze();

            // Mostrar errores sintácticos si los hay
            if (!syntaxSuccess) {
                System.out.println("\nErrores sintácticos encontrados:");
                System.out.println("------------------------------");
                for (String error : syntaxAnalyzer.getSyntaxErrors()) {
                    System.out.println(error);
                }

                JOptionPane.showMessageDialog(null,
                        "Se encontraron errores sintácticos. Revise la consola para más detalles.",
                        "Errores Sintácticos", JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println("\nAnálisis sintáctico completado exitosamente.");

                // Mostrar tabla de símbolos
                System.out.println("\nTabla de Símbolos:");
                System.out.println("----------------");
                System.out.println(syntaxAnalyzer.getSymbolTable().toString());

                // Verificar errores semánticos
                List<String> semanticErrors = syntaxAnalyzer.getSymbolTable().getSemanticErrors();
                if (!semanticErrors.isEmpty()) {
                    System.out.println("\nErrores semánticos encontrados:");
                    System.out.println("-----------------------------");
                    for (String error : semanticErrors) {
                        System.out.println(error);
                    }

                    JOptionPane.showMessageDialog(null,
                            "Se encontraron errores semánticos. Revise la consola para más detalles.",
                            "Errores Semánticos", JOptionPane.ERROR_MESSAGE);
                } else {
                    System.out.println("\nAnálisis semántico completado exitosamente.");
                    JOptionPane.showMessageDialog(null,
                            "Análisis léxico, sintáctico y semántico completados exitosamente.",
                            "Análisis Completo", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception e) {
            System.err.println("Error al ejecutar el analizador: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al ejecutar el analizador: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
