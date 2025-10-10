import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionTest {

    @Test
    void testSimpleAdd() throws ParseException {
        Expression e = Parser.parse("(2 + 3)");
        assertEquals(5, e.eval(""), "(2 + 3) должно быть 5");
    }

    @Test
    void testNestedExpression() throws ParseException {
        Expression e = Parser.parse("(3 + (2 * 5))"); // 3 + (2 * 5)
        assertEquals(13, e.eval(""), "(3 + (2 * 5)) должно быть 13");
    }

    @Test
    void testWithVariable() throws ParseException {
        Expression e = Parser.parse("(3 + (2 * x))");
        assertEquals(23, e.eval("x = 10"), "(3 + (2 * x)) при x=10 должно быть 23");
    }

    @Test
    void testSubtraction() throws ParseException {
        Expression e = Parser.parse("(10 - (2 * 3))");
        assertEquals(4, e.eval(""), "(10 - (2 * 3)) должно быть 4");
    }

    @Test
    void testDivision() throws ParseException {
        Expression e = Parser.parse("((20 / 4) + (6 / 3))");
        assertEquals(7, e.eval(""), "((20 / 4) + (6 / 3)) должно быть 7");
    }

    @Test
    void testDivisionByZero() throws ParseException {
        Expression e = Parser.parse("(10 / (x - 5))");
        assertThrows(ArithmeticException.class, () -> e.eval("x = 5"),
                "Деление на ноль должно вызвать ArithmeticException");
    }

    @Test
    void testDerivativeConstant() throws ParseException {
        Expression e = Parser.parse("5");
        Expression de = e.derivative("x");
        assertEquals(0, de.eval("x = 10"), "Производная константы должна быть 0");
    }

    @Test
    void testDerivativeVariable() throws ParseException {
        Expression e = Parser.parse("x");
        Expression de = e.derivative("x");
        assertEquals(1, de.eval("x = 10"), "Производная x должна быть 1");
    }

    @Test
    void testDerivativePolynomial() throws ParseException {
        Expression e = Parser.parse("(((x * x) + (2 * x)) + 1)");
        Expression de = e.derivative("x"); // 2*x + 2
        assertEquals(12, de.eval("x = 5"), "Производная (x*x + 2*x + 1) при x=5 должна быть 12");
    }

    @Test
    void testMultipleVariables() throws ParseException {
        Expression e = Parser.parse("((x + y) * z)");
        assertEquals(35, e.eval("x = 2; y = 3; z = 7"),
                "((x + y) * z) при x=2, y=3, z=7 должно быть 35");
    }

    @Test
    void testWhitespaceTolerance() throws ParseException {
        Expression e = Parser.parse(" ( 3 + ( 2 * x ) ) ");
        assertEquals(23, e.eval("x = 10; y = 13"),
                "Парсер должен корректно обрабатывать пробелы");
    }

    @Test
    void testInvalidSyntax() {
        assertThrows(ParseException.class, () -> Parser.parse("2 + + 3"),
                "Некорректное выражение должно вызвать ParseException");
    }
}
