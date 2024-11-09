package LLVM.Type;

public class ArrayType extends IRType {
    private int length;
    private IRType type;

    public ArrayType(int length, IRType type) {
        this.length = length;
        this.type = type;
    }

    public IRType getElmType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public String toString() {
        return "[" + length + " x " + type.toString() + "]";
    }

    @Override
    public boolean isArray() {
        return true;
    }
}
