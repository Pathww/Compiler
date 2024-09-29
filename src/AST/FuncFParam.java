package AST;

import Lexer.Token;

public class FuncFParam {
    private BType bType;
    private Token ident;

    private Token lbrack = null;
    private Token rbrack = null;

    public FuncFParam(BType bType, Token ident) {
        this.bType = bType;
        this.ident = ident;
    }

    public FuncFParam(BType bType, Token ident, Token lbrack, Token rbrack) {
        this.bType = bType;
        this.ident = ident;
        this.lbrack = lbrack;
        this.rbrack = rbrack;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(ident.toString());
        if (lbrack != null) {
            sb.append(lbrack);
            sb.append(rbrack);
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
