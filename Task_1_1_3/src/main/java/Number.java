import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class Number extends Expression {
    private final int value;

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Expression derivative(String var) {
        return new Number(0);
    }

    @Override
    protected int eval(Map<String, Integer> vars) {
        return value;
    }
}
