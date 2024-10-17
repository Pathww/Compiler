package AST;

import Lexer.Token;
import Symbol.SymbolTable;

import java.util.ArrayList;

public class ConstDecl {
    private Token constTk;
    private BType bType;
    private ArrayList<ConstDef> constDefs;
    private ArrayList<Token> commas;
    private Token semicn;

    public ConstDecl(Token constTk, BType bType, ArrayList<ConstDef> constDefs, ArrayList<Token> commas, Token semicn) {
        this.constTk = constTk;
        this.bType = bType;
        this.constDefs = constDefs;
        this.commas = commas;
        this.semicn = semicn;
    }

    public void toSymbol(SymbolTable table) {
        for (ConstDef c : constDefs) {
            c.toSymbol(table, bType);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(constTk.toString());
        sb.append(bType.toString());

        sb.append(constDefs.get(0).toString());
        for (int i = 0; i < commas.size(); i++) {
            sb.append(commas.get(i).toString());
            sb.append(constDefs.get(i + 1).toString());
        }

        sb.append(semicn.toString());
        sb.append("<ConstDecl>\n");
        return sb.toString();
    }
}
