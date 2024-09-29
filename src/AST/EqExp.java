package AST;

import Lexer.Token;

public class EqExp {
    private RelExp relExp = null;
    private EqExp eqExp = null;
    private Token op = null;

    public EqExp(RelExp relExp) {
        this.relExp = relExp;
    }

    public EqExp(EqExp eqExp, Token op, RelExp relExp) {
        this.eqExp = eqExp;
        this.op = op;
        this.relExp = relExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (eqExp != null) {
            sb.append(eqExp);
            sb.append(op.toString());
        }
        sb.append(relExp.toString());
        sb.append("<EqExp>\n");
        return sb.toString();
    }
}
