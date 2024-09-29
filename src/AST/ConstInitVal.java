package AST;

import Lexer.Token;

import java.util.ArrayList;

public class ConstInitVal {
    private ConstExp constExp = null;

    private Token lbrace = null;
    private ArrayList<ConstExp> constExps = null;
    private ArrayList<Token> commas = null;
    private Token rbrace = null;

    private Token stringConst = null;

    public ConstInitVal(ConstExp constExp) {
        this.constExp = constExp;
    }

    public ConstInitVal(Token lbrace, ArrayList<ConstExp> constExps, ArrayList<Token> commas, Token rbrace) {
        this.lbrace = lbrace;
        this.constExps = constExps;
        this.commas = commas;
        this.rbrace = rbrace;
    }

    public ConstInitVal(Token stringConst) {
        this.stringConst = stringConst;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constExp != null) {
            sb.append(constExp);
        } else if (stringConst != null) {
            sb.append(stringConst);
        } else {
            sb.append(lbrace);
            if (!constExps.isEmpty()) {
                sb.append(constExps.get(0));
                for (int i = 0; i < commas.size(); i++) {
                    sb.append(commas.get(i));
                    sb.append(constExps.get(i + 1));
                }
            }
            sb.append(rbrace);
        }

        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}

