package AST;

import Lexer.Token;

public class LOrExp {
    private LAndExp lAndExp = null;
    private LOrExp lOrExp = null;
    private Token or = null;

    public LOrExp(LAndExp lAndExp) {
        this.lAndExp = lAndExp;
    }

    public LOrExp(LOrExp lOrExp, Token or, LAndExp lAndExp) {
        this.lOrExp = lOrExp;
        this.or = or;
        this.lAndExp = lAndExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lOrExp != null) {
            sb.append(lOrExp);
            sb.append(or.toString());
        }
        sb.append(lAndExp.toString());
        sb.append("<LOrExp>\n");
        return sb.toString();
    }
}
