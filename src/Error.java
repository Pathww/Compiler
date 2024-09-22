public class Error {
    private int line;
    private String type;

    public Error(int line, String type) {
        this.line = line;
        this.type = type;
    }

    @Override
    public String toString() {
        return line + " " + type;
    }
}
