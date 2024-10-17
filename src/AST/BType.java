package AST;

import Lexer.Token;
import Lexer.TokenType;

public class BType {
    private Token type;
    // 'int' or 'char'

    public BType(Token type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public TokenType getType() {
        return type.getType();
    }
}
