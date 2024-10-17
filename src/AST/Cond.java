package AST;

import Symbol.SymbolTable;

public class Cond {
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public void toSymbol(SymbolTable table) {
        lOrExp.toSymbol(table);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lOrExp);
        sb.append("<Cond>\n");
        return sb.toString();
    }
}
