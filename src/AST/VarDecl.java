package AST;

import Lexer.Token;

import java.util.ArrayList;

public class VarDecl {
    private BType bType;
    private ArrayList<VarDef> varDefs;
    private ArrayList<Token> commas;
    private Token semicn;

    public VarDecl(BType bType, ArrayList<VarDef> varDefs, ArrayList<Token> commas, Token semicn) {
        this.bType = bType;
        this.varDefs = varDefs;
        this.commas = commas;
        this.semicn = semicn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(varDefs.get(0).toString());
        for (int i = 0; i < commas.size(); i++) {
            sb.append(commas.get(i));
            sb.append(varDefs.get(i + 1).toString());
        }
        sb.append(semicn.toString());

        sb.append("<VarDecl>\n");
        return sb.toString();
    }
}
