package LLVM.Type;

public class IntegerType extends IRType {
    public static IntegerType I64 = new IntegerType(64);
    public static IntegerType I32 = new IntegerType(32);
    public static IntegerType I8 = new IntegerType(8);
    public static IntegerType I1 = new IntegerType(1);
    public static IntegerType VOID = new IntegerType(0);

    private int bits;

    private IntegerType(int bits) {
        this.bits = bits;
    }

    public String toString() {
        if (bits == 0) {
            return "void";
        } else {
            return "i" + bits;
        }
    }
}
