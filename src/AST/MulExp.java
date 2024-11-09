package AST;

import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Lexer.TokenType;
import Symbol.SymbolTable;

public class MulExp {
    private UnaryExp unaryExp = null;

    private Token op = null;
    private MulExp mulExp = null;

    public MulExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }

    public MulExp(MulExp mulExp, Token op, UnaryExp unaryExp) {
        this.mulExp = mulExp;
        this.op = op;
        this.unaryExp = unaryExp;
    }

    public LVal toLVal() {
        return unaryExp.toLVal();
    }

    public void toSymbol(SymbolTable table) {
        if (mulExp != null) {
            mulExp.toSymbol(table);
        }
        unaryExp.toSymbol(table);
    }

    public Value buildIR() {
        if (mulExp != null) { //AddExp ('+' | '−') MulExp
            Value left = mulExp.buildIR();
            Value right = unaryExp.buildIR();
            if (left.getType() != IntegerType.I32) { // SEXT???
                left = IRBuilder.addConvertInst(InstrType.ZEXT, left, IntegerType.I32);
            }
            if (right.getType() != IntegerType.I32) {
                right = IRBuilder.addConvertInst(InstrType.ZEXT, right, IntegerType.I32);
            }
            if (op.getType() == TokenType.MULT) {
                return IRBuilder.addBinaryInst(InstrType.MUL, left, right);
            } else if (op.getType() == TokenType.DIV) {
                return IRBuilder.addBinaryInst(InstrType.SDIV, left, right);
            } else {
                return IRBuilder.addBinaryInst(InstrType.SREM, left, right);
            }
        }
        return unaryExp.buildIR();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mulExp != null) {
            sb.append(mulExp);
            sb.append(op.toString());
        }
        sb.append(unaryExp.toString());
        sb.append("<MulExp>\n");
        return sb.toString();
    }

    public int getParaType(SymbolTable table) {
        return unaryExp.getParaType(table);
    }

    public Integer calVal() {
        if (mulExp != null) {
            if (op.getType() == TokenType.MULT) {
                return mulExp.calVal() * unaryExp.calVal();
            } else if (op.getType() == TokenType.DIV) {
                return mulExp.calVal() / unaryExp.calVal();
            } else {
                return mulExp.calVal() % unaryExp.calVal();
            }
        } else {
            return unaryExp.calVal();
        }
    }
}
