package AST;

import Lexer.Token;

public class StmtAssign implements Stmt {
    private LVal lVal;
    private Token assign;
    private Exp exp;
    private Token semicn;

    public StmtAssign(LVal lVal, Token assign, Exp exp, Token semicn) {
        this.lVal = lVal;
        this.assign = assign;
        this.exp = exp;
        this.semicn = semicn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal);
        sb.append(assign);
        sb.append(exp);
        sb.append(semicn);
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
