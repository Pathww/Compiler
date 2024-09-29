package AST;

public class Exp {
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }


    public LVal toLVal() {
        return addExp.toLVal();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp);
        sb.append("<Exp>\n");
        return sb.toString();
    }
}
