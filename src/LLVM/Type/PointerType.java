package LLVM.Type;

public class PointerType extends IRType {
    private IRType type;

    public PointerType(IRType type) {
        this.type = type;
    }

    public IRType getRefType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString() + "*";
    }
}
