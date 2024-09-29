package AST;

import Lexer.Token;

import java.util.ArrayList;

public class StmtPrintf implements Stmt {
    private Token printfTk;
    private Token lparent;
    private Token stringConst;
    private ArrayList<Token> commas;
    private ArrayList<Exp> exps;
    private Token rparent;
    private Token semicn;

    public StmtPrintf(Token printfTk, Token lparent, Token stringConst, ArrayList<Token> commas, ArrayList<Exp> exps, Token rparent, Token semicn) {
        this.printfTk = printfTk;
        this.lparent = lparent;
        this.stringConst = stringConst;
        this.commas = commas;
        this.exps = exps;
        this.rparent = rparent;
        this.semicn = semicn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(printfTk.toString());
        sb.append(lparent.toString());
        sb.append(stringConst.toString());
        for (int i = 0; i < commas.size(); i++) {
            sb.append(commas.get(i).toString());
            sb.append(exps.get(i).toString());
        }
        sb.append(rparent.toString());
        sb.append(semicn.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
