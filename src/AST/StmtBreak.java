package AST;

import Lexer.Token;

public class StmtBreak implements Stmt{
    private Token breakTk;
    private Token semicn;
    public StmtBreak(Token breakTk, Token semicn) {
        this.breakTk = breakTk;
        this.semicn = semicn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(breakTk.toString());
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
