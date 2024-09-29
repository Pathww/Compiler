package Lexer;

public enum TokenType {
    IDENFR,     ELSETK,    VOIDTK,  SEMICN,
    INTCON,     NOT,       MULT,    COMMA,
    STRCON,     AND,       DIV,     LPARENT,
    CHRCON,     OR,        MOD,     RPARENT,
    MAINTK,     FORTK,     LSS,     LBRACK,
    CONSTTK,    GETINTTK,  LEQ,     RBRACK,
    INTTK,      GETCHARTK, GRE,     LBRACE,
    CHARTK,     PRINTFTK,  GEQ,     RBRACE,
    BREAKTK,    RETURNTK,  EQL,
    CONTINUETK, PLUS,      NEQ,
    IFTK,       MINU,      ASSIGN,
    EOFTK
}
