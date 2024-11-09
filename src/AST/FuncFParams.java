package AST;

import Lexer.Token;
import Symbol.SymbolTable;

import java.util.ArrayList;

public class FuncFParams {
    private ArrayList<FuncFParam> funcFParams;
    private ArrayList<Token> commas;

    public FuncFParams(ArrayList<FuncFParam> funcFParams, ArrayList<Token> commas) {
        this.funcFParams = funcFParams;
        this.commas = commas;
    }

    public void toSymbol(SymbolTable table) {
        for (FuncFParam f : funcFParams) {
            f.toSymbol(table);
        }
    }

    public void buildIR() {
        for (FuncFParam f : funcFParams) {
            f.buildIR();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcFParams.get(0).toString());
        for (int i = 0; i < commas.size(); i++) {
            sb.append(commas.get(i).toString());
            sb.append(funcFParams.get(i + 1).toString());
        }
        sb.append("<FuncFParams>\n");
        return sb.toString();
    }
}
