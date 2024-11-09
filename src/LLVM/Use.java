package LLVM;

public class Use {
    private User user;
    private Value value;
    private int pos;

    public Use(Value value, int pos, User user) {
        this.user = user;
        this.value = value;
        this.pos = pos;
    }
}
