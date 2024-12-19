package LLVM;

public class Use {
    public User user;
    public Value value;
    public int pos;

    public Use(Value value, int pos, User user) {
        this.value = value;
        this.pos = pos;
        this.user = user;
    }
}
