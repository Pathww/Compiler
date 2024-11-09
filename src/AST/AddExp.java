package AST;

import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Lexer.TokenType;
import Symbol.SymbolTable;

public class AddExp {
    private MulExp mulExp = null;
    private AddExp addExp = null;
    private Token op = null;

    public AddExp(MulExp mulExp) {
        this.mulExp = mulExp;
    }

    public AddExp(AddExp addExp, Token op, MulExp mulExp) {
        this.addExp = addExp;
        this.op = op;
        this.mulExp = mulExp;
    }

    public LVal toLVal() {
        return mulExp.toLVal();
    }

    public void toSymbol(SymbolTable table) {
        if (addExp != null) {
            addExp.toSymbol(table);
        }
        mulExp.toSymbol(table);
    }

    public Value buildIR() {
        if (addExp != null) { //AddExp ('+' | '−') MulExp
            Value left = addExp.buildIR();
            Value right = mulExp.buildIR();
            if (left.getType() != IntegerType.I32) { // SEXT???
                left = IRBuilder.addConvertInst(InstrType.ZEXT, left, IntegerType.I32);
            }
            if (right.getType() != IntegerType.I32) {
                right = IRBuilder.addConvertInst(InstrType.ZEXT, right, IntegerType.I32);
            }
            if (op.getType() == TokenType.PLUS) {
                return IRBuilder.addBinaryInst(InstrType.ADD, left, right);
            } else {
                return IRBuilder.addBinaryInst(InstrType.SUB, left, right);
            }
        }
        return mulExp.buildIR();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (addExp != null) {
            sb.append(addExp);
            sb.append(op.toString());
        }
        sb.append(mulExp.toString());
        sb.append("<AddExp>\n");
        return sb.toString();
    }


    public int getParaType(SymbolTable table) {
        return mulExp.getParaType(table);
    }

    public int calVal() {
        if (addExp != null) {
            if (op.getType() == TokenType.PLUS) {
                return addExp.calVal() + mulExp.calVal();
            } else {
                return addExp.calVal() - mulExp.calVal();
            }
        } else {
            return mulExp.calVal();
        }
    }
}
