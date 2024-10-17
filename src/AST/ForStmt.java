package AST;

import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

public class ForStmt {
    private LVal lVal;
    private Token assign;
    private Exp exp;

    public ForStmt(LVal lVal, Token assign, Exp exp) {
        this.lVal = lVal;
        this.assign = assign;
        this.exp = exp;
    }

    public void toSymbol(SymbolTable table) {
        lVal.toSymbol(table);
        exp.toSymbol(table);
        if (table.isConst(lVal.getIdent().getValue())) {
            ErrorHandler.addError(lVal.getIdent().getLine(), ErrorType.h);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal);
        sb.append(assign);
        sb.append(exp);
        sb.append("<ForStmt>\n");
        return sb.toString();
    }
}
