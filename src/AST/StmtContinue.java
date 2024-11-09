package AST;

import LLVM.BasicBlock;
import LLVM.IRBuilder;
import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

public class StmtContinue implements Stmt {
    private Token continueTk;
    private Token semicn;

    public StmtContinue(Token continueTk, Token semicn) {
        this.continueTk = continueTk;
        this.semicn = semicn;
    }

    public void toSymbol(SymbolTable table) {
        if (SymbolTable.loop == 0) {
            ErrorHandler.addError(continueTk.getLine(), ErrorType.m);
        }
    }

    public void buildIR() {
        BasicBlock basicBlock = IRBuilder.getForBlock();
        IRBuilder.addBranchInst(basicBlock);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(continueTk.toString());
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
