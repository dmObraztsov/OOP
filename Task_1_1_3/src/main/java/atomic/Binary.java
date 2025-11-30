package atomic;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Binary extends Expression {
    protected final Expression left;
    protected final Expression right;
}
