package AST;

public class Cond {
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lOrExp);
        sb.append("<Cond>\n");
        return sb.toString();
    }
}
