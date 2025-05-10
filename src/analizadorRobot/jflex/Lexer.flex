import java_cup.runtime.*;
import java.util.ArrayList;

%%

%class Lexer
%unicode
%cup
%line
%column
%public

%{
    // Lista para almacenar todos los tokens encontrados
    private ArrayList<Token> tokens = new ArrayList<>();
    
    // Método para añadir tokens a la lista
    public void addToken(String lexeme, String tokenType, int line, int column) {
        tokens.add(new Token(lexeme, tokenType, line+1, column+1));
    }
    
    // Método para obtener todos los tokens encontrados
    public ArrayList<Token> getTokens() {
        return tokens;
    }
    
    // Método para reportar errores
    private void error(String message) {
        System.out.println("Error léxico en línea " + (yyline+1) + ", columna " + (yycolumn+1) + ": " + message);
    }
    
    // Variables de contexto
    private boolean despuesDePunto = false;
%}

/* Estados léxicos */
%state AFTER_DOT

/* Definición de patrones básicos */
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
Identifier = [a-zA-Z][a-zA-Z0-9_]*
IntNumber = 0 | [1-9][0-9]*
FloatNumber = [0-9]+ \. [0-9]* | \. [0-9]+
Number = {IntNumber} | {FloatNumber}

%%

/* Estado inicial */
<YYINITIAL> {
    /* Palabras reservadas */
    "Robot"                  { 
                               addToken(yytext(), "PALABRA_RESERVADA", yyline, yycolumn); 
                               return new Symbol(sym.ROBOT, yyline, yycolumn, yytext()); 
                             }
    
    /* Operadores y delimitadores */
    "."                      { 
                               yybegin(AFTER_DOT);
                               despuesDePunto = true;
                               addToken(yytext(), "DELIMITADOR", yyline, yycolumn); 
                               return new Symbol(sym.PUNTO, yyline, yycolumn, yytext()); 
                             }
    "="                      { 
                               addToken(yytext(), "OPERADOR", yyline, yycolumn); 
                               return new Symbol(sym.IGUAL, yyline, yycolumn, yytext()); 
                             }
    "("                      { 
                               addToken(yytext(), "DELIMITADOR", yyline, yycolumn); 
                               return new Symbol(sym.PARENTESIS_A, yyline, yycolumn, yytext()); 
                             }
    ")"                      { 
                               addToken(yytext(), "DELIMITADOR", yyline, yycolumn); 
                               return new Symbol(sym.PARENTESIS_C, yyline, yycolumn, yytext()); 
                             }
    "{"                      { 
                               addToken(yytext(), "DELIMITADOR", yyline, yycolumn); 
                               return new Symbol(sym.LLAVE_A, yyline, yycolumn, yytext()); 
                             }
    "}"                      { 
                               addToken(yytext(), "DELIMITADOR", yyline, yycolumn); 
                               return new Symbol(sym.LLAVE_C, yyline, yycolumn, yytext()); 
                             }
    ","                      { 
                               addToken(yytext(), "DELIMITADOR", yyline, yycolumn); 
                               return new Symbol(sym.COMA, yyline, yycolumn, yytext()); 
                             }
    ";"                      { 
                               addToken(yytext(), "DELIMITADOR", yyline, yycolumn); 
                               return new Symbol(sym.PUNTO_COMA, yyline, yycolumn, yytext()); 
                             }
    "+"                      { 
                               addToken(yytext(), "OPERADOR", yyline, yycolumn); 
                               return new Symbol(sym.ERROR, yyline, yycolumn, yytext()); 
                             }
    "-"                      { 
                               addToken(yytext(), "OPERADOR", yyline, yycolumn); 
                               return new Symbol(sym.ERROR, yyline, yycolumn, yytext()); 
                             }
    "*"                      { 
                               addToken(yytext(), "OPERADOR", yyline, yycolumn); 
                               return new Symbol(sym.ERROR, yyline, yycolumn, yytext()); 
                             }
    "/"                      { 
                               addToken(yytext(), "OPERADOR", yyline, yycolumn); 
                               return new Symbol(sym.ERROR, yyline, yycolumn, yytext()); 
                             }
    
    /* Identificadores y números */
    {Identifier}             { 
                               addToken(yytext(), "IDENTIFICADOR", yyline, yycolumn); 
                               return new Symbol(sym.IDENTIFICADOR, yyline, yycolumn, yytext()); 
                             }
    
    {IntNumber}              { 
                               try {
                                   int value = Integer.parseInt(yytext());
                                   addToken(yytext(), "NUMERO", yyline, yycolumn); 
                                   return new Symbol(sym.NUMERO, yyline, yycolumn, value); 
                               } catch (NumberFormatException e) {
                                   error("Número fuera de rango: " + yytext());
                                   addToken(yytext(), "ERROR", yyline, yycolumn);
                                   return new Symbol(sym.ERROR, yyline, yycolumn, "Número fuera de rango: " + yytext());
                               }
                             }
    
    {FloatNumber}            { 
                               try {
                                   float value = Float.parseFloat(yytext());
                                   addToken(yytext(), "NUMERO", yyline, yycolumn); 
                                   return new Symbol(sym.NUMERO, yyline, yycolumn, Math.round(value)); 
                               } catch (NumberFormatException e) {
                                   error("Número flotante inválido: " + yytext());
                                   addToken(yytext(), "ERROR", yyline, yycolumn);
                                   return new Symbol(sym.ERROR, yyline, yycolumn, "Número flotante inválido: " + yytext());
                               }
                             }
    
    /* Ignorar espacios en blanco */
    {WhiteSpace}             { /* Ignorar espacios */ }
}

/* Estado después de un punto (para métodos y propiedades) */
<AFTER_DOT> {
    "iniciar"                { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "METODO", yyline, yycolumn); 
                               return new Symbol(sym.INICIAR, yyline, yycolumn, yytext()); 
                             }
    
    "detener"                { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "METODO", yyline, yycolumn); 
                               return new Symbol(sym.DETENER, yyline, yycolumn, yytext()); 
                             }
    
    "base"                   { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "PROPIEDAD", yyline, yycolumn); 
                               return new Symbol(sym.BASE, yyline, yycolumn, yytext()); 
                             }
    
    "cuerpo"                 { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "PROPIEDAD", yyline, yycolumn); 
                               return new Symbol(sym.CUERPO, yyline, yycolumn, yytext()); 
                             }
    
    "garra"                  { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "PROPIEDAD", yyline, yycolumn); 
                               return new Symbol(sym.GARRA, yyline, yycolumn, yytext()); 
                             }
    
    "velocidad"              { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "PROPIEDAD", yyline, yycolumn); 
                               return new Symbol(sym.VELOCIDAD, yyline, yycolumn, yytext()); 
                             }
    
    "abrirGarra"             { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "METODO", yyline, yycolumn); 
                               return new Symbol(sym.ABRIR_GARRA, yyline, yycolumn, yytext()); 
                             }
    
    "cerrarGarra"            { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "METODO", yyline, yycolumn); 
                               return new Symbol(sym.CERRAR_GARRA, yyline, yycolumn, yytext()); 
                             }
    
    "repetir"                { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "METODO", yyline, yycolumn); 
                               return new Symbol(sym.REPETIR, yyline, yycolumn, yytext()); 
                             }
    
    /* Identificadores después de punto (posible error de sintaxis) */
    {Identifier}             { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               addToken(yytext(), "DESCONOCIDO", yyline, yycolumn); 
                               return new Symbol(sym.ERROR, yyline, yycolumn, "Método o propiedad desconocida: " + yytext()); 
                             }
    
    /* Otros caracteres después de punto (posibles errores) */
    .                        { 
                               yybegin(YYINITIAL);
                               despuesDePunto = false;
                               error("Símbolo inesperado después de punto: " + yytext());
                               addToken(yytext(), "ERROR", yyline, yycolumn);
                               return new Symbol(sym.ERROR, yyline, yycolumn, "Símbolo inesperado después de punto: " + yytext());
                             }
    
    /* Espacios en blanco mantienen el estado AFTER_DOT */
    {WhiteSpace}             { /* Ignorar espacios */ }
}

/* Manejo de errores (cualquier carácter no reconocido) */
[^]                      { 
                           error("Símbolo no reconocido: " + yytext());
                           addToken(yytext(), "ERROR", yyline, yycolumn);
                           return new Symbol(sym.ERROR, yyline, yycolumn, "Símbolo no reconocido: " + yytext());
                         }