package AST;

import Lexer.Token;
import Symbol.SymbolTable;

public class StmtIf implements Stmt {
    private Token ifTk;
    private Token lparent;
    private Cond cond;
    private Token rparent;
    private Stmt ifStmt;
    private Token elseTk = null;
    private Stmt elseStmt = null;

    public StmtIf(Token ifTk, Token lparent, Cond cond, Token rparent, Stmt ifStmt) {
        this.ifTk = ifTk;
        this.lparent = lparent;
        this.cond = cond;
        this.rparent = rparent;
        this.ifStmt = ifStmt;
    }

    public StmtIf(Token ifTk, Token lparent, Cond cond, Token rparent, Stmt ifStmt, Token elseTk, Stmt elseStmt) {
        this.ifTk = ifTk;
        this.lparent = lparent;
        this.cond = cond;
        this.rparent = rparent;
        this.ifStmt = ifStmt;
        this.elseTk = elseTk;
        this.elseStmt = elseStmt;
    }

    public void toSymbol(SymbolTable table) {
        cond.toSymbol(table);
        ifStmt.toSymbol(table);
        if (elseStmt != null) {
            elseStmt.toSymbol(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ifTk.toString());
        sb.append(lparent.toString());
        sb.append(cond.toString());
        sb.append(rparent.toString());
        sb.append(ifStmt.toString());
        if (elseStmt != null) {
            sb.append(elseTk.toString());
            sb.append(elseStmt.toString());
        }
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
