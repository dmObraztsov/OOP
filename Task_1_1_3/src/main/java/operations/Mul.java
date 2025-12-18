package operations;

import atomic.Binary;
import atomic.Expression;
import java.util.Map;


public final class Mul extends Binary {
    public Mul(Expression l, Expression r) {
        super(l, r);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "*" + right.toString() + ")";
    }

    @Override
    public Expression derivative(String var) {
        return new Add(new Mul(left.derivative(var), right), new Mul(left, right.derivative(var)));
    }

    @Override
    public int eval(Map<String, Integer> vars) {
        return left.eval(vars) * right.eval(vars);
    }
}
