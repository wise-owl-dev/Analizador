import java_cup.runtime.*;
import java.util.ArrayList;
import analizadorRobot.Token;
import analizadorRobot.jcup.sym;

%%

%class Lexico
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
%}

// Definición de patrones básicos
L = [a-zA-Z]
D = [0-9]
WhiteSpace = [ \t\r\n\f]
Identifier = {L}({L}|{D})*
Number = {D}+

%%

/* Palabras reservadas */
"Robot"                  { addToken(yytext(), "PALABRA_RESERVADA", yyline, yycolumn); return new Symbol(sym.ROBOT, yyline, yycolumn, yytext()); }
"iniciar"                { addToken(yytext(), "METODO", yyline, yycolumn); return new Symbol(sym.INICIAR, yyline, yycolumn, yytext()); }
"detener"                { addToken(yytext(), "METODO", yyline, yycolumn); return new Symbol(sym.DETENER, yyline, yycolumn, yytext()); }
"base"                   { addToken(yytext(), "PROPIEDAD", yyline, yycolumn); return new Symbol(sym.BASE, yyline, yycolumn, yytext()); }
"cuerpo"                 { addToken(yytext(), "PROPIEDAD", yyline, yycolumn); return new Symbol(sym.CUERPO, yyline, yycolumn, yytext()); }
"garra"                  { addToken(yytext(), "PROPIEDAD", yyline, yycolumn); return new Symbol(sym.GARRA, yyline, yycolumn, yytext()); }
"velocidad"              { addToken(yytext(), "PROPIEDAD", yyline, yycolumn); return new Symbol(sym.VELOCIDAD, yyline, yycolumn, yytext()); }
"abrirGarra"             { addToken(yytext(), "METODO", yyline, yycolumn); return new Symbol(sym.ABRIR_GARRA, yyline, yycolumn, yytext()); }
"cerrarGarra"            { addToken(yytext(), "METODO", yyline, yycolumn); return new Symbol(sym.CERRAR_GARRA, yyline, yycolumn, yytext()); }
"repetir"                { addToken(yytext(), "METODO", yyline, yycolumn); return new Symbol(sym.REPETIR, yyline, yycolumn, yytext()); }

/* Operadores y delimitadores */
"."                      { addToken(yytext(), "DELIMITADOR", yyline, yycolumn); return new Symbol(sym.PUNTO, yyline, yycolumn, yytext()); }
"="                      { addToken(yytext(), "OPERADOR", yyline, yycolumn); return new Symbol(sym.IGUAL, yyline, yycolumn, yytext()); }
"("                      { addToken(yytext(), "DELIMITADOR", yyline, yycolumn); return new Symbol(sym.PARENTESIS_A, yyline, yycolumn, yytext()); }
")"                      { addToken(yytext(), "DELIMITADOR", yyline, yycolumn); return new Symbol(sym.PARENTESIS_C, yyline, yycolumn, yytext()); }
"{"                      { addToken(yytext(), "DELIMITADOR", yyline, yycolumn); return new Symbol(sym.LLAVE_A, yyline, yycolumn, yytext()); }
"}"                      { addToken(yytext(), "DELIMITADOR", yyline, yycolumn); return new Symbol(sym.LLAVE_C, yyline, yycolumn, yytext()); }
","                      { addToken(yytext(), "DELIMITADOR", yyline, yycolumn); return new Symbol(sym.COMA, yyline, yycolumn, yytext()); }
";"                      { addToken(yytext(), "DELIMITADOR", yyline, yycolumn); return new Symbol(sym.PUNTO_COMA, yyline, yycolumn, yytext()); }

/* Identificadores y números */
{Identifier}             { addToken(yytext(), "IDENTIFICADOR", yyline, yycolumn); return new Symbol(sym.IDENTIFICADOR, yyline, yycolumn, yytext()); }
{Number}                 { 
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

/* Ignorar espacios en blanco */
{WhiteSpace}             { /* Ignorar espacios */ }

/* Manejo de errores */
[^]                      { 
                           error("Símbolo no reconocido: " + yytext());
                           addToken(yytext(), "ERROR", yyline, yycolumn);
                           return new Symbol(sym.ERROR, yyline, yycolumn, "Símbolo no reconocido: " + yytext());
                         }