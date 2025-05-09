package main.java.robotcontrol;
import static robotcontrol.Token.*;
import java.util.ArrayList;

%%
%class AnalizadorLexico
%type Token
%line
%column
%unicode
%public

%{
    // Almacena todos los lexemas encontrados
    private ArrayList<String> lexemes = new ArrayList<>();
    // Almacena la línea de cada token
    private ArrayList<Integer> lines = new ArrayList<>();
    // Almacena la columna de cada token
    private ArrayList<Integer> columns = new ArrayList<>();
    // Almacena todos los tokens encontrados
    private ArrayList<Token> tokens = new ArrayList<>();
    
    /**
     * Agrega un token encontrado a las listas
     */
    private void addToken(Token token, String lexeme) {
        tokens.add(token);
        lexemes.add(lexeme);
        lines.add(yyline + 1);  // +1 porque JFlex comienza en 0
        columns.add(yycolumn + 1);  // +1 porque JFlex comienza en 0
    }
    
    /**
     * Obtiene todos los tokens encontrados
     */
    public ArrayList<Token> getTokens() {
        return tokens;
    }
    
    /**
     * Obtiene todos los lexemas encontrados
     */
    public ArrayList<String> getLexemes() {
        return lexemes;
    }
    
    /**
     * Obtiene todas las líneas donde se encontraron tokens
     */
    public ArrayList<Integer> getLines() {
        return lines;
    }
    
    /**
     * Obtiene todas las columnas donde se encontraron tokens
     */
    public ArrayList<Integer> getColumns() {
        return columns;
    }
    
    /**
     * Obtiene una representación de texto de todos los tokens encontrados
     */
    public String getTokensText() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            result.append(String.format("Línea %d, Columna %d: Token: %s, Lexema: %s\n", 
                          lines.get(i), columns.get(i), tokens.get(i).getName(), lexemes.get(i)));
        }
        return result.toString();
    }
%}

// Definición de expresiones regulares básicas
L = [a-zA-Z]
D = [0-9]
WHITE_SPACE = [ \t\r\n]

// Regex para identificadores y números
Identifier = {L}({L}|{D})*
Number = ("-")?{D}+("."{D}+)?

%%

/* Palabras reservadas */
"Robot"         { addToken(ROBOT, yytext()); return ROBOT; }
"iniciar"       { addToken(INICIAR, yytext()); return INICIAR; }
"detener"       { addToken(DETENER, yytext()); return DETENER; }

/* Métodos */
"base"          { addToken(BASE, yytext()); return BASE; }
"cuerpo"        { addToken(CUERPO, yytext()); return CUERPO; }
"garra"         { addToken(GARRA, yytext()); return GARRA; }
"velocidad"     { addToken(VELOCIDAD, yytext()); return VELOCIDAD; }
"abrirGarra"    { addToken(ABRIR_GARRA, yytext()); return ABRIR_GARRA; }
"cerrarGarra"   { addToken(CERRAR_GARRA, yytext()); return CERRAR_GARRA; }
"repetir"       { addToken(REPETIR, yytext()); return REPETIR; }

/* Símbolos */
"."             { addToken(PUNTO, yytext()); return PUNTO; }
"="             { addToken(IGUAL, yytext()); return IGUAL; }
"("             { addToken(PARENTESIS_ABIERTO, yytext()); return PARENTESIS_ABIERTO; }
")"             { addToken(PARENTESIS_CERRADO, yytext()); return PARENTESIS_CERRADO; }
"{"             { addToken(LLAVE_ABIERTA, yytext()); return LLAVE_ABIERTA; }
"}"             { addToken(LLAVE_CERRADA, yytext()); return LLAVE_CERRADA; }
";"             { addToken(SEMI_COLON, yytext()); return SEMI_COLON; }

/* Identificadores y números */
{Identifier}    { addToken(IDENTIFICADOR, yytext()); return IDENTIFICADOR; }
{Number}        { addToken(NUMERO, yytext()); return NUMERO; }

/* Ignorar espacios en blanco */
{WHITE_SPACE}   { /* Ignorar */ }

/* Cualquier otro carácter es un error */
[^]             { addToken(ERROR, yytext()); return ERROR; }