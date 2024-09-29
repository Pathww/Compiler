package Error;

public class Error {
    private int line;
    private ErrorType type;

    public Error(int line, ErrorType type) {
        this.line = line;
        this.type = type;
    }

    @Override
    public String toString() {
        return line + " " + type + "\n";
    }
}
