package LLVM;

public class SlotTracker {
    public static int no = 0;

    public static int alloc() {
        return (no++);
    }

    public static void reset() {
        no = 0;
    }
}
