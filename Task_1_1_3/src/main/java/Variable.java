import java.util.Map;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public final class Variable extends Expression {
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Expression derivative(String var) {
        return new Number(name.equals(var) ? 1 : 0);
    }

    @Override
    protected int eval(Map<String, Integer> vars) {
        if (!vars.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' is not assigned");
        }
        return vars.get(name);
    }
}
