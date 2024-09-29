package AST;

import Lexer.Token;

import java.util.ArrayList;

public class InitVal {
    private Exp exp = null;

    private Token lbrace = null;
    private ArrayList<Exp> exps = null;
    private ArrayList<Token> commas = null;
    private Token rbrace = null;

    private Token stringConst = null;

    public InitVal(Exp exp) {
        this.exp = exp;
    }

    public InitVal(Token lbrace, ArrayList<Exp> exps, ArrayList<Token> commas, Token rbrace) {
        this.lbrace = lbrace;
        this.exps = exps;
        this.commas = commas;
        this.rbrace = rbrace;
    }

    public InitVal(Token stringConst) {
        this.stringConst = stringConst;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp != null) {
            sb.append(exp);
        } else if (stringConst != null) {
            sb.append(stringConst);
        } else {
            sb.append(lbrace);
            if (!exps.isEmpty()) {
                sb.append(exps.get(0));
                for (int i = 0; i < commas.size(); i++) {
                    sb.append(commas.get(i));
                    sb.append(exps.get(i + 1));
                }
            }
            sb.append(rbrace);
        }

        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
