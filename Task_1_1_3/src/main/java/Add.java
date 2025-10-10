import java.util.Map;

public final class Add extends Binary {
    public Add(Expression l, Expression r) {
        super(l, r);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "+" + right.toString() + ")";
    }

    @Override
    public Expression derivative(String var) {
        return new Add(left.derivative(var), right.derivative(var));
    }

    @Override
    protected int eval(Map<String, Integer> vars) {
        return left.eval(vars) + right.eval(vars);
    }
}
