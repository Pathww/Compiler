package AST;

import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

public class StmtReturn implements Stmt {
    private Token returnTk;
    private Exp exp = null;
    private Token semicn;

    public StmtReturn(Token returnTk, Token semicn) {
        this.returnTk = returnTk;
        this.semicn = semicn;
    }

    public StmtReturn(Token returnTk, Exp exp, Token semicn) {
        this.returnTk = returnTk;
        this.exp = exp;
        this.semicn = semicn;
    }

    public void toSymbol(SymbolTable table) {
        if (exp != null) {
            if (table.isVoid()) {
                ErrorHandler.addError(returnTk.getLine(), ErrorType.f);
            }
            exp.toSymbol(table);
        }
    }

    public void buildIR() {
        if (exp != null) {
            Value value = exp.buildIR();
            if (IRBuilder.getCurFunction().getType() != value.getType()) {
                if (IRBuilder.getCurFunction().getType() == IntegerType.I32) {
                    value = IRBuilder.addConvertInst(InstrType.ZEXT, value, IntegerType.I32);
                } else {
                    value = IRBuilder.addConvertInst(InstrType.TRUNC, value, IntegerType.I8);
                }
            }
            IRBuilder.addReturnInst(value);
        } else {
            IRBuilder.addReturnInst();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnTk.toString());
        if (exp != null) {
            sb.append(exp);
        }
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
