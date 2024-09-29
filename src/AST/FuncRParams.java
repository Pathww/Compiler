package AST;

import Lexer.Token;

import java.util.ArrayList;

public class FuncRParams {
    private ArrayList<Exp> exps;
    private ArrayList<Token> commas;

    public FuncRParams(ArrayList<Exp> exps, ArrayList<Token> commas) {
        this.exps = exps;
        this.commas = commas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(exps.get(0).toString());
        for (int i = 0; i < commas.size(); i++) {
            sb.append(commas.get(i).toString());
            sb.append(exps.get(i + 1).toString());
        }
        sb.append("<FuncRParams>\n");
        return sb.toString();
    }
}
