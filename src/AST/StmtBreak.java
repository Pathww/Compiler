package AST;

import LLVM.BasicBlock;
import LLVM.IRBuilder;
import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

public class StmtBreak implements Stmt {
    private Token breakTk;
    private Token semicn;

    public StmtBreak(Token breakTk, Token semicn) {
        this.breakTk = breakTk;
        this.semicn = semicn;
    }

    public void toSymbol(SymbolTable table) {
        if (SymbolTable.loop == 0) {
            ErrorHandler.addError(breakTk.getLine(), ErrorType.m);
        }
    }

    public void buildIR() {
        BasicBlock basicBlock = IRBuilder.getLastBlock();
        IRBuilder.addBranchInst(basicBlock);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(breakTk.toString());
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
