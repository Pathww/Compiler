package AST;

public class BlockItem {
    private Decl decl = null;
    private Stmt stmt = null;

    public BlockItem(Decl decl) {
        this.decl = decl;
    }

    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
    }

    @Override
    public String toString() {
        if (decl != null) {
            return decl.toString();
        } else {
            return stmt.toString();
        }
    }
}
