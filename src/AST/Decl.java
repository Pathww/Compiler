package AST;

public class Decl {
    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;

    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }

    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constDecl != null) {
            sb.append(constDecl.toString());
        } else {
            sb.append(varDecl.toString());
        }
        return sb.toString();
    }
}
