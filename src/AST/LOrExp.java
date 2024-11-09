package AST;

import LLVM.BasicBlock;
import LLVM.IRBuilder;
import Lexer.Token;
import Symbol.SymbolTable;

public class LOrExp {
    private LAndExp lAndExp = null;
    private LOrExp lOrExp = null;
    private Token or = null;

    public LOrExp(LAndExp lAndExp) {
        this.lAndExp = lAndExp;
    }

    public LOrExp(LOrExp lOrExp, Token or, LAndExp lAndExp) {
        this.lOrExp = lOrExp;
        this.or = or;
        this.lAndExp = lAndExp;
    }

    public void toSymbol(SymbolTable table) {
        if (lOrExp != null) {
            lOrExp.toSymbol(table);
        }
        lAndExp.toSymbol(table);
    }

    public void buildIR(BasicBlock trueBlock, BasicBlock falseBlock) {
        if (lOrExp != null) {
            BasicBlock nextBlock = new BasicBlock();
            lOrExp.buildIR(trueBlock, nextBlock);
            IRBuilder.addBasicBlock(nextBlock);
        }
        lAndExp.buildIR(trueBlock, falseBlock);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lOrExp != null) {
            sb.append(lOrExp);
            sb.append(or.toString());
        }
        sb.append(lAndExp.toString());
        sb.append("<LOrExp>\n");
        return sb.toString();
    }
}
