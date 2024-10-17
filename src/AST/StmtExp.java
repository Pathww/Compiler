package AST;

import Lexer.Token;
import Symbol.SymbolTable;

public class StmtExp implements Stmt {
    private Exp exp = null;
    private Token semicn;

    public StmtExp(Token semicn) {
        this.semicn = semicn;
    }

    public StmtExp(Exp exp, Token semicn) {
        this.exp = exp;
        this.semicn = semicn;
    }

    public void toSymbol(SymbolTable table) {
        if (exp != null) {
            exp.toSymbol(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp != null) {
            sb.append(exp);
        }
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
