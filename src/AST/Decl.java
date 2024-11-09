package AST;

import Symbol.SymbolTable;

public class Decl {
    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;

    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }

    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }

    public void toSymbol(SymbolTable table) {
        if (constDecl != null) {
            constDecl.toSymbol(table);
        } else {
            varDecl.toSymbol(table);
        }
    }

    public void buildIR() {
        if (constDecl != null) {
            constDecl.buildIR();
        } else {
            varDecl.buildIR();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constDecl != null) {
            sb.append(constDecl);
        } else {
            sb.append(varDecl.toString());
        }
        return sb.toString();
    }
}
