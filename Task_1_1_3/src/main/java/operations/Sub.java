package operations;

import atomic.Binary;
import atomic.Expression;
import java.util.Map;

public final class Sub extends Binary {
    public Sub(Expression l, Expression r) {
        super(l, r);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "-" + right.toString() + ")";
    }

    @Override
    public Expression derivative(String var) {
        return new Sub(left.derivative(var), right.derivative(var));
    }

    @Override
    public int eval(Map<String, Integer> vars) {
        return left.eval(vars) - right.eval(vars);
    }
}
