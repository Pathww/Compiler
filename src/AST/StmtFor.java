package AST;

import Lexer.Token;

public class StmtFor implements Stmt {
    private Token forTk;
    private Token lparent;
    private ForStmt forStmt1 = null;
    private Token semicn1;
    private Cond cond = null;
    private Token semicn2;
    private ForStmt forStmt2 = null;
    private Token rparent;
    private Stmt stmt;

    public StmtFor(Token forTk, Token lparent, ForStmt forStmt1, Token semicn1, Cond cond, Token semicn2, ForStmt forStmt2,Token rparent, Stmt stmt) {
        this.forTk = forTk;
        this.lparent = lparent;
        this.forStmt1 = forStmt1;
        this.semicn1 = semicn1;
        this.cond = cond;
        this.semicn2 = semicn2;
        this.forStmt2 = forStmt2;
        this.rparent = rparent;
        this.stmt = stmt;
    }


    public void addForStmt1(ForStmt forStmt1) {
        this.forStmt1 = forStmt1;
    }

    public void addForStmt2(ForStmt forStmt2) {
        this.forStmt2 = forStmt2;
    }

    public void addCond(Cond cond) {
        this.cond = cond;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(forTk.toString());
        sb.append(lparent.toString());
        if (forStmt1 != null) {
            sb.append(forStmt1.toString());
        }
        sb.append(semicn1.toString());
        if (cond != null) {
            sb.append(cond.toString());
        }
        sb.append(semicn2.toString());
        if (forStmt2 != null) {
            sb.append(forStmt2.toString());
        }
        sb.append(rparent.toString());
        sb.append(stmt.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
