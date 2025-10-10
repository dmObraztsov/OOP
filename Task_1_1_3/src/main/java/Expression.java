import java.util.HashMap;
import java.util.Map;

public abstract class Expression {
    public void print() {
        System.out.println(this);
    }

    public abstract Expression derivative(String var);

    public int eval(String assignments) {
        Map<String, Integer> map = parseAssignments(assignments);
        return eval(map);
    }

    protected abstract int eval(Map<String, Integer> vars);

    private static Map<String, Integer> parseAssignments(String s) {
        Map<String, Integer> m = new HashMap<>();
        if (s == null || s.trim().isEmpty()) {
            return m;
        }
        String[] parts = s.split(";");
        for (String p : parts) {
            String t = p.trim();
            if (t.isEmpty()) {
                continue;
            }
            String[] kv = t.split("=");
            if (kv.length != 2) {
                throw new RuntimeException("Bad assignment: " + p);
            }
            String name = kv[0].trim();
            String valStr = kv[1].trim();
            int v;
            try {
                v = Integer.parseInt(valStr);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Bad number in assignment: " + valStr);
            }
            m.put(name, v);
        }
        return m;
    }
}
