package Error;

import java.util.ArrayList;
import java.util.Comparator;

public class ErrorHandler {
    private ArrayList<Error> errors;

    private Comparator<Error> cmp = (o1, o2) -> o1.getLine() - o2.getLine();


    public ErrorHandler() {
        errors = new ArrayList<>();
    }

    public void addError(int line, ErrorType type) {
        errors.add(new Error(line, type));
    }

    @Override
    public String toString() {
        errors.sort(cmp);
        StringBuilder sb = new StringBuilder();
        for (Error e : errors) {
            sb.append(e.toString());
        }
        return sb.toString();
    }
}
