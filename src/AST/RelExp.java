package AST;

import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Lexer.TokenType;
import Symbol.SymbolTable;

public class RelExp {
    private AddExp addExp = null;
    private RelExp relExp = null;
    private Token op = null;

    public RelExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public RelExp(RelExp relExp, Token op, AddExp addExp) {
        this.relExp = relExp;
        this.op = op;
        this.addExp = addExp;
    }

    public void toSymbol(SymbolTable table) {
        if (relExp != null) {
            relExp.toSymbol(table);
        }
        addExp.toSymbol(table);
    }

    public Value buildIR() {
        if (relExp != null) {
            Value left = relExp.buildIR();
            Value right = addExp.buildIR();
            if (left.getType() != IntegerType.I32) {
                left = IRBuilder.addConvertInst(InstrType.ZEXT, left, IntegerType.I32);
            }
            if (right.getType() != IntegerType.I32) {
                right = IRBuilder.addConvertInst(InstrType.ZEXT, right, IntegerType.I32);
            }
            if (op.getType() == TokenType.LSS) {
                return IRBuilder.addCmpInst(InstrType.SLT, left, right);
            } else if (op.getType() == TokenType.GRE) {
                return IRBuilder.addCmpInst(InstrType.SGT, left, right);
            } else if (op.getType() == TokenType.LEQ) {
                return IRBuilder.addCmpInst(InstrType.SLE, left, right);
            } else {
                return IRBuilder.addCmpInst(InstrType.SGE, left, right);
            }
        }
        return addExp.buildIR();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (relExp != null) {
            sb.append(relExp);
            sb.append(op.toString());
        }
        sb.append(addExp.toString());
        sb.append("<RelExp>\n");
        return sb.toString();
    }
}
