package AST;

import Symbol.SymbolTable;

public class BlockItem {
    private Decl decl = null;
    private Stmt stmt = null;

    public BlockItem(Decl decl) {
        this.decl = decl;
    }

    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
    }

    public void toSymbol(SymbolTable table) {
        if (decl != null) {
            decl.toSymbol(table);
        } else {
            stmt.toSymbol(table);
        }
    }

    public void buildIR() {
        if (decl != null) {
            decl.buildIR();
        } else {
            stmt.buildIR();
        }
    }

    @Override
    public String toString() {
        if (decl != null) {
            return decl.toString();
        } else {
            return stmt.toString();
        }
    }

    public boolean isReturn() {
        if (stmt != null) {
            return stmt instanceof StmtReturn;
        }
        return false;
    }
}
