package AST;

import Lexer.Token;
import Symbol.SymbolTable;
import Error.*;

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

    public void toSymbol(SymbolTable table) {
        String str = stringConst.getValue();
        int cnt = 0;
        for (int i = 0; i + 1 < str.length(); i++) {
            if (str.charAt(i) == '%' && (str.charAt(i + 1) == 'd' || str.charAt(i + 1) == 'c')) {
                cnt++;
            }
        }
        if (cnt != exps.size()) {
            ErrorHandler.addError(printfTk.getLine(), ErrorType.l);
        }

        for (Exp exp : exps) {
            exp.toSymbol(table);
        }
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
