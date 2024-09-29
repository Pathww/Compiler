package AST;

import Lexer.Token;

public class StmtContinue implements Stmt {
    private Token continueTk;
    private Token semicn;

    public StmtContinue(Token continueTk, Token semicn) {
        this.continueTk = continueTk;
        this.semicn = semicn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(continueTk.toString());
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
