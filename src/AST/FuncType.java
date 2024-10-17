package AST;

import Lexer.*;

public class FuncType {
    public Token type;
    // 'void' or 'int' or 'char'

    public FuncType(Token type) {
        this.type = type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.toString());
        sb.append("<FuncType>\n");
        return sb.toString();
    }

    public TokenType getType() {
        return type.getType();
    }
}
