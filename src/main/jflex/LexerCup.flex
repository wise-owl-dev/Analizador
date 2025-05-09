package robotcontrol;
import java_cup.runtime.*;
import static robotcontrol.ParserSym.*;

%%
%class LexerCup
%type java_cup.runtime.Simbolo
%cup
%full
%line
%column
%unicode
%public

%{
    // Método para crear un símbolo para Cup
    private Simbolo Simbolo(int type, Object value) {
        return new Simbolo(type, yyline + 1, yycolumn + 1, value);
    }
    
    // Método para crear un símbolo sin valor
    private Simbolo Simbolo(int type) {
        return new Simbolo(type, yyline + 1, yycolumn + 1);
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
"Robot"         { return Simbolo(ROBOT, yytext()); }
"iniciar"       { return Simbolo(INICIAR, yytext()); }
"detener"       { return Simbolo(DETENER, yytext()); }

/* Métodos */
"base"          { return Simbolo(BASE, yytext()); }
"cuerpo"        { return Simbolo(CUERPO, yytext()); }
"garra"         { return Simbolo(GARRA, yytext()); }
"velocidad"     { return Simbolo(VELOCIDAD, yytext()); }
"abrirGarra"    { return Simbolo(ABRIR_GARRA, yytext()); }
"cerrarGarra"   { return Simbolo(CERRAR_GARRA, yytext()); }
"repetir"       { return Simbolo(REPETIR, yytext()); }

/* Símbolos */
"."             { return Simbolo(PUNTO, yytext()); }
"="             { return Simbolo(IGUAL, yytext()); }
"("             { return Simbolo(PARENTESIS_ABIERTO, yytext()); }
")"             { return Simbolo(PARENTESIS_CERRADO, yytext()); }
"{"             { return Simbolo(LLAVE_ABIERTA, yytext()); }
"}"             { return Simbolo(LLAVE_CERRADA, yytext()); }
";"             { return Simbolo(SEMI_COLON, yytext()); }

/* Identificadores y números */
{Identifier}    { return Simbolo(IDENTIFICADOR, yytext()); }
{Number}        { return Simbolo(NUMERO, yytext()); }

/* Ignorar espacios en blanco */
{WHITE_SPACE}   { /* Ignorar */ }

/* Cualquier otro carácter es un error */
[^]             { return Simbolo(ERROR, yytext()); }