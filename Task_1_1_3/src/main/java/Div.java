import java.util.Map;

public final class Div extends Binary {
    public Div(Expression l, Expression r) {
        super(l, r);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "/" + right.toString() + ")";
    }

    @Override
    public Expression derivative(String var) {
        Expression u = left;
        Expression v = right;
        Expression num = new Sub(new Mul(u.derivative(var), v), new Mul(u, v.derivative(var)));
        Expression den = new Mul(v, v);
        return new Div(num, den);
    }

    @Override
    protected int eval(Map<String, Integer> vars) {
        int numerator = left.eval(vars);
        int denominator = right.eval(vars);
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero during eval");
        }
        return numerator / denominator;
    }
}
