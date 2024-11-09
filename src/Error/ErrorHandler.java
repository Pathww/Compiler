package Error;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class ErrorHandler {
    private static ArrayList<Error> errors = new ArrayList<>();

    private static HashSet<Integer> lines = new HashSet<>();

    private static Comparator<Error> cmp = (o1, o2) -> o1.getLine() - o2.getLine();

    public static void addError(int line, ErrorType type) {
        if (lines.contains(line)) {
            return;
        }
        errors.add(new Error(line, type));
        lines.add(line);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public String toString() {
        errors.sort(cmp);
        StringBuilder sb = new StringBuilder();
        for (Error e : errors) {
            sb.append(e);
        }
        return sb.toString();
    }
}
