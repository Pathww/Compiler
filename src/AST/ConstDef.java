package AST;

import Lexer.*;
import Symbol.*;
import Error.*;

public class ConstDef {
    private Token ident;

    private Token lbrack = null;
    private ConstExp constExp = null;
    private Token rbrack = null;

    private Token assign;
    private ConstInitVal constInitVal;

    public ConstDef(Token ident, Token assign, ConstInitVal constInitVal) {
        this.ident = ident;
        this.assign = assign;
        this.constInitVal = constInitVal;
    }

    public ConstDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack, Token assign, ConstInitVal constInitVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.constInitVal = constInitVal;
    }

    public void toSymbol(SymbolTable table, BType bType) {
        SymbolType type;
        if (lbrack != null) {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.ConstIntArray;
            } else {
                type = SymbolType.ConstCharArray;
            }
            constExp.toSymbol(table);
        } else {
            if (bType.getType() == TokenType.INTTK) {
                type = SymbolType.ConstInt;
            } else {
                type = SymbolType.ConstChar;
            }
        }
        if (!table.addSymbol(new Symbol(ident.getValue(), type))) {
            ErrorHandler.addError(ident.getLine(), ErrorType.b);
        }
        constInitVal.toSymbol(table);
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
        sb.append(assign);
        sb.append(constInitVal);

        sb.append("<ConstDef>\n");
        return sb.toString();
    }
}
