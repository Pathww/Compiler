package AST;

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
}
