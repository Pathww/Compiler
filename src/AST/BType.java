package AST;

import Lexer.Token;

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
}
