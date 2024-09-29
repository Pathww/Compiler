package AST;

import Lexer.Token;

public class VarDef {
    private Token ident;
    private Token lbrack = null;
    private ConstExp constExp = null;
    private Token rbrack = null;
    private Token assign = null;
    private InitVal initVal = null;

    public VarDef(Token ident) {
        this.ident = ident;
    }

    public VarDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
    }

    public VarDef(Token ident, Token assign, InitVal initVal) {
        this.ident = ident;
        this.assign = assign;
        this.initVal = initVal;
    }

    public VarDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack, Token assign, InitVal initVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.initVal = initVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (constExp != null) {
            sb.append(lbrack);
            sb.append(constExp);
            sb.append(rbrack);
        }
        if (initVal != null) {
            sb.append(assign);
            sb.append(initVal);
        }
        sb.append("<VarDef>\n");
        return sb.toString();
    }
}
