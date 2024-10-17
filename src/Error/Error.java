package Error;

public class Error {
    private int line;
    private ErrorType type;

    public Error(int line, ErrorType type) {
        this.line = line;
        this.type = type;
    }

    public int getLine() {
        return line;
    }

    public ErrorType getType() {
        return type;
    }

    @Override
    public String toString() {
        return line + " " + type + "\n";
    }
}
