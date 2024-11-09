package AST;

import LLVM.ConstInteger;
import LLVM.Type.IntegerType;
import LLVM.Value;
import Lexer.Token;

public class Number {
    private Token intConst;

    public Number(Token intConst) {
        this.intConst = intConst;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intConst.toString());
        sb.append("<Number>\n");
        return sb.toString();
    }

    public int calVal() {
        return Integer.parseInt(intConst.getValue());
    }

    public Value buildIR() {
        return new ConstInteger(Integer.parseInt(intConst.getValue()), IntegerType.I32);
    }
}
