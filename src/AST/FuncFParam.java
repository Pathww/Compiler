package AST;

import Lexer.*;
import Symbol.*;
import Error.*;

public class FuncFParam {
    private BType bType;
    private Token ident;

    private Token lbrack = null;
    private Token rbrack = null;

    public FuncFParam(BType bType, Token ident) {
        this.bType = bType;
        this.ident = ident;
    }

    public FuncFParam(BType bType, Token ident, Token lbrack, Token rbrack) {
        this.bType = bType;
        this.ident = ident;
        this.lbrack = lbrack;
        this.rbrack = rbrack;
    }

    public void toSymbol(SymbolTable table) {
        SymbolType type;
        if (lbrack != null) {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.IntArray;
            } else {
                type = SymbolType.CharArray;
            }
        } else {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.Int;
            } else {
                type = SymbolType.Char;
            }
        }
        if (!table.addSymbol(new Symbol(ident.getValue(), type))) {
            ErrorHandler.addError(ident.getLine(), ErrorType.b);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(ident.toString());
        if (lbrack != null) {
            sb.append(lbrack);
            sb.append(rbrack);
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
