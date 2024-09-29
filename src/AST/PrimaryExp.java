package AST;

import Lexer.Token;

public class PrimaryExp {
    private Token lparent = null;
    private Exp exp = null;
    private Token rparent = null;

    private LVal lVal = null;

    private Number number = null;

    private Character character = null;

    public PrimaryExp(Token lparent, Exp exp, Token rparent) {
        this.lparent = lparent;
        this.exp = exp;
        this.rparent = rparent;
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
    }

    public PrimaryExp(Number number) {
        this.number = number;
    }

    public PrimaryExp(Character character) {
        this.character = character;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lVal != null) {
            sb.append(lVal);
        } else if (number != null) {
            sb.append(number);
        } else if (character != null) {
            sb.append(character);
        } else {
            sb.append(lparent);
            sb.append(exp);
            sb.append(rparent);
        }
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}

