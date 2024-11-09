package AST;

import Lexer.Token;
import Lexer.TokenType;

public class UnaryOp {
    private Token op;

    public UnaryOp(Token op) {
        this.op = op;
    }

    public TokenType getType() {
        return op.getType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(op);
        sb.append("<UnaryOp>\n");
        return sb.toString();
    }
}
