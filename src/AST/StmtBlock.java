package AST;

public class StmtBlock implements Stmt{
    private Block block;
    public StmtBlock(Block block) {
        this.block = block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(block);
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
