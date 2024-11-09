package AST;

import LLVM.BasicBlock;
import LLVM.ConstInteger;
import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Symbol.SymbolTable;

public class LAndExp {
    private EqExp eqExp = null;
    private LAndExp lAndExp = null;
    private Token and = null;

    public LAndExp(EqExp eqExp) {
        this.eqExp = eqExp;
    }

    public LAndExp(LAndExp lAndExp, Token and, EqExp eqExp) {
        this.lAndExp = lAndExp;
        this.and = and;
        this.eqExp = eqExp;
    }

    public void toSymbol(SymbolTable table) {
        if (lAndExp != null) {
            lAndExp.toSymbol(table);
        }
        eqExp.toSymbol(table);
    }

    public void buildIR(BasicBlock trueBlock, BasicBlock falseBlock) {
        if (lAndExp != null) {
            BasicBlock nextBlock = new BasicBlock();
            lAndExp.buildIR(nextBlock, falseBlock);
            IRBuilder.addBasicBlock(nextBlock);
        }
        Value value = eqExp.buildIR();
        if (value.getType() != IntegerType.I1) {
            Value cmp = IRBuilder.addCmpInst(InstrType.NE, value, new ConstInteger(0, value.getType()));
            IRBuilder.addBranchInst(cmp, trueBlock, falseBlock);
        } else {
            IRBuilder.addBranchInst(value, trueBlock, falseBlock);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lAndExp != null) {
            sb.append(lAndExp);
            sb.append(and.toString());
        }
        sb.append(eqExp.toString());

        sb.append("<LAndExp>\n");
        return sb.toString();
    }
}
