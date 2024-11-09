package AST;

import LLVM.IRBuilder;
import LLVM.Instr.InstrType;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;
import Lexer.TokenType;
import Symbol.SymbolTable;

public class EqExp {
    private RelExp relExp = null;
    private EqExp eqExp = null;
    private Token op = null;

    public EqExp(RelExp relExp) {
        this.relExp = relExp;
    }

    public EqExp(EqExp eqExp, Token op, RelExp relExp) {
        this.eqExp = eqExp;
        this.op = op;
        this.relExp = relExp;
    }

    public void toSymbol(SymbolTable table) {
        if (eqExp != null) {
            eqExp.toSymbol(table);
        }
        relExp.toSymbol(table);
    }

    public Value buildIR() {
        if (eqExp != null) {
            Value left = eqExp.buildIR();
            Value right = relExp.buildIR();
            if (left.getType() != IntegerType.I32) {
                left = IRBuilder.addConvertInst(InstrType.ZEXT, left, IntegerType.I32);
            }
            if (right.getType() != IntegerType.I32) {
                right = IRBuilder.addConvertInst(InstrType.ZEXT, right, IntegerType.I32);
            }
            if (op.getType() == TokenType.EQL) {
                return IRBuilder.addCmpInst(InstrType.EQ, left, right);
            } else {
                return IRBuilder.addCmpInst(InstrType.NE, left, right);
            }
        }
        return relExp.buildIR();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (eqExp != null) {
            sb.append(eqExp);
            sb.append(op.toString());
        }
        sb.append(relExp.toString());
        sb.append("<EqExp>\n");
        return sb.toString();
    }
}
