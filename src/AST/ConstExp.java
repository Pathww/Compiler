package AST;

import Symbol.SymbolTable;

public class ConstExp {
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public void toSymbol(SymbolTable table) {
        addExp.toSymbol(table);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());

        sb.append("<ConstExp>\n");
        return sb.toString();
    }

    public int calVal() {
        return addExp.calVal();
    }
}
