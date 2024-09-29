package AST;

import Lexer.Token;

public class UnaryOp {
    private Token op;

    public UnaryOp(Token op) {
        this.op = op;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(op);
        sb.append("<UnaryOp>\n");
        return sb.toString();
    }
}
