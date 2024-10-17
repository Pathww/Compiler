package AST;

import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

public class StmtGet implements Stmt {
    private LVal lVal;
    private Token assign;
    private Token getTk; // 'getint' or 'getchar'
    private Token lparent;
    private Token rparent;
    private Token semicn;

    public StmtGet(LVal lVal, Token assign, Token getTk, Token lparent, Token rparent, Token semicn) {
        this.lVal = lVal;
        this.assign = assign;
        this.getTk = getTk;
        this.lparent = lparent;
        this.rparent = rparent;
        this.semicn = semicn;
    }

    public void toSymbol(SymbolTable table) {
        lVal.toSymbol(table);
        if (table.isConst(lVal.getIdent().getValue())) {
            ErrorHandler.addError(lVal.getIdent().getLine(), ErrorType.h);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append(assign.toString());
        sb.append(getTk.toString());
        sb.append(lparent.toString());
        sb.append(rparent.toString());
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
