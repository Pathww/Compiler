package AST;

import Lexer.*;
import Symbol.*;
import Error.*;

public class VarDef {
    private Token ident;
    private Token lbrack = null;
    private ConstExp constExp = null;
    private Token rbrack = null;
    private Token assign = null;
    private InitVal initVal = null;

    public VarDef(Token ident) {
        this.ident = ident;
    }

    public VarDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
    }

    public VarDef(Token ident, Token assign, InitVal initVal) {
        this.ident = ident;
        this.assign = assign;
        this.initVal = initVal;
    }

    public VarDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack, Token assign, InitVal initVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.initVal = initVal;
    }

    public void toSymbol(SymbolTable table, BType bType) {
        SymbolType type;
        if (lbrack != null) {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.IntArray;
            } else {
                type = SymbolType.CharArray;
            }
            constExp.toSymbol(table);
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
        if (initVal != null) {
            initVal.toSymbol(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (lbrack != null) {
            sb.append(lbrack);
            sb.append(constExp);
            sb.append(rbrack);
        }
        if (initVal != null) {
            sb.append(assign);
            sb.append(initVal);
        }
        sb.append("<VarDef>\n");
        return sb.toString();
    }
}
