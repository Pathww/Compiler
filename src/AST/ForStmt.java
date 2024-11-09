package AST;

import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Type.PointerType;
import LLVM.Value;
import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

public class ForStmt {
    private LVal lVal;
    private Token assign;
    private Exp exp;

    public ForStmt(LVal lVal, Token assign, Exp exp) {
        this.lVal = lVal;
        this.assign = assign;
        this.exp = exp;
    }

    public void toSymbol(SymbolTable table) {
        lVal.toSymbol(table);
        exp.toSymbol(table);
        if (table.isConst(lVal.getIdent().getValue())) {
            ErrorHandler.addError(lVal.getIdent().getLine(), ErrorType.h);
        }
    }

    public void buildIR() {
        Value lValue = lVal.buildIR();
        Value expValue = exp.buildIR();
        if (expValue.getType() != ((PointerType) lValue.getType()).getRefType()) {
            if (expValue.getType() == IntegerType.I8) {
                expValue = IRBuilder.addConvertInst(InstrType.ZEXT, expValue, IntegerType.I32);
            } else {
                expValue = IRBuilder.addConvertInst(InstrType.TRUNC, expValue, IntegerType.I8);
            }
        }
        IRBuilder.addStoreInst(expValue, lValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal);
        sb.append(assign);
        sb.append(exp);
        sb.append("<ForStmt>\n");
        return sb.toString();
    }
}
