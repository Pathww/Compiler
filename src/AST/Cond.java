package AST;

import LLVM.BasicBlock;
import Symbol.SymbolTable;

public class Cond {
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public void toSymbol(SymbolTable table) {
        lOrExp.toSymbol(table);
    }

    public void buildIR(BasicBlock trueBlock, BasicBlock falseBlock) {
        lOrExp.buildIR(trueBlock, falseBlock);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lOrExp);
        sb.append("<Cond>\n");
        return sb.toString();
    }
}
