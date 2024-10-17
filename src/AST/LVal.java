package AST;

import Lexer.*;
import Symbol.*;
import Error.*;

public class LVal {
    private Token ident;

    private Token lbrack = null;
    private Exp exp = null;
    private Token rbrack = null;

    public LVal(Token ident) {
        this.ident = ident;
    }

    public LVal(Token ident, Token lbrack, Exp exp, Token rbrack) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.exp = exp;
        this.rbrack = rbrack;
    }

    public void toSymbol(SymbolTable table) {
        if (table.getSymbol(ident.getValue()) == null) {
            ErrorHandler.addError(ident.getLine(), ErrorType.c);
        }
        if (exp != null) {
            exp.toSymbol(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (exp != null) {
            sb.append(lbrack);
            sb.append(exp);
            sb.append(rbrack);
        }
        sb.append("<LVal>\n");
        return sb.toString();
    }

    public Token getIdent() {
        return ident;
    }

    public int getParaType(SymbolTable table) {
        Symbol symbol = table.getSymbol(ident.getValue()); // null?
        if (symbol.getType() == SymbolType.IntArray && lbrack == null) {
            return 1;
        } else if (symbol.getType() == SymbolType.CharArray && lbrack == null) {
            return 2;
        } else {
            return 0;
        }
    }
}
