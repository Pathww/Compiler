package AST;

import LLVM.Value;
import Symbol.SymbolTable;

public class Exp {
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }


    public LVal toLVal() {
        return addExp.toLVal();
    }

    public void toSymbol(SymbolTable table) {
        addExp.toSymbol(table);
    }

    public Value buildIR() {
        return addExp.buildIR();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp);
        sb.append("<Exp>\n");
        return sb.toString();
    }

    public int getParaType(SymbolTable table) {
        return addExp.getParaType(table);
    }

    public int calVal() {
        return addExp.calVal();
    }
}
