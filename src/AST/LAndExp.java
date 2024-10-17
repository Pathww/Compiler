package AST;

import Lexer.Token;
import Symbol.SymbolTable;

public class LAndExp {
    private EqExp eqExp = null;
    private LAndExp lAndExp = null;
    private Token and = null;

    public LAndExp(EqExp eqExp) {
        this.eqExp = eqExp;
    }

    public LAndExp(LAndExp lAndExp, Token and, EqExp eqExp) {
        this.lAndExp = lAndExp;
        this.and = and;
        this.eqExp = eqExp;
    }

    public void toSymbol(SymbolTable table) {
        if (lAndExp != null) {
            lAndExp.toSymbol(table);
        }
        eqExp.toSymbol(table);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lAndExp != null) {
            sb.append(lAndExp);
            sb.append(and.toString());
        }
        sb.append(eqExp.toString());

        sb.append("<LAndExp>\n");
        return sb.toString();
    }
}
