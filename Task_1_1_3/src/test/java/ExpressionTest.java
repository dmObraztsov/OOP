import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ExpressionTest {

    @Test
    void testSimpleAdd() throws ParseException {
        Expression e = Parser.parse("(2 + 3)");
        assertEquals(5, e.eval(""), "(2 + 3) должно быть 5");
    }

    @Test
    void testSimpleMul() throws ParseException {
        Expression e = Parser.parse("(2 * 3)");
        assertEquals(6, e.eval(""), "(2 * 3) должно быть 6");
    }

    @Test
    void testSimpleSub() throws ParseException {
        Expression e = Parser.parse("(3 - 2)");
        assertEquals(1, e.eval(""), "(3 - 2) должно быть 1");
    }

    @Test
    void testSimpleDiv() throws ParseException {
        Expression e = Parser.parse("(8 / 4)");
        assertEquals(2, e.eval(""), "(8 / 4) должно быть 2");
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

    @Test
    void testSubPositiveNumbers() throws ParseException {
        Expression e = Parser.parse("(10 - 3)");
        assertEquals(7, e.eval(""), "(10 - 3) должно быть 7");
    }

    @Test
    void testSubNegativeNumbers() throws ParseException {
        Expression e = Parser.parse("((0 - 5) - (0 - 3))");
        assertEquals(-2, e.eval(""), "(-5 - (-3)) должно быть -2");
    }

    @Test
    void testSubWithVariable() throws ParseException {
        Expression e = Parser.parse("(x - 7)");
        assertEquals(3, e.eval("x = 10"), "(x - 7) при x=10 должно быть 3");
    }

    @Test
    void testSubNestedExpression() throws ParseException {
        Expression e = Parser.parse("((x - 3) - (y - 2))"); // (x-3)-(y-2) = x - y - 1
        assertEquals(4, e.eval("x = 10; y = 5"),
                "((x - 3) - (y - 2)) при x=10, y=5 должно быть 4");
    }

    @Test
    void testSubDerivative() throws ParseException {
        Expression e = Parser.parse("(x - 5)");
        Expression de = e.derivative("x");
        assertEquals(1, de.eval("x = 10"), "Производная (x - 5) должна быть 1");
    }

    @Test
    void testDivPositiveNumbers() throws ParseException {
        Expression e = Parser.parse("(20 / 4)");
        assertEquals(5, e.eval(""), "(20 / 4) должно быть 5");
    }

    @Test
    void testDivNegativeNumbers() throws ParseException {
        Expression e = Parser.parse("((0 - 10) / 2)");
        assertEquals(-5, e.eval(""), "(-10 / 2) должно быть -5");
    }

    @Test
    void testDivWithVariables() throws ParseException {
        Expression e = Parser.parse("(x / y)");
        assertEquals(3, e.eval("x = 9; y = 3"), "(x / y) при x=9, y=3 должно быть 3");
    }

    @Test
    void testDivNestedExpression() throws ParseException {
        Expression e = Parser.parse("((x + y) / (x - y))");
        assertEquals(1, e.eval("x = 8; y = 2"),
                "((x + y) / (x - y)) при x=8, y=2 должно быть 1 при целочисленном делении");
    }

    @Test
    void testDivZeroNumerator() throws ParseException {
        Expression e = Parser.parse("(0 / x)");
        assertEquals(0, e.eval("x = 10"), "(0 / x) всегда должно быть 0");
    }

    @Test
    void testDivByZeroThrows() throws ParseException {
        Expression e = Parser.parse("(x / (y - 2))");
        assertThrows(ArithmeticException.class, () -> e.eval("x = 5; y = 2"),
                "Деление на ноль должно вызывать ArithmeticException");
    }

    @Test
    void testDivByNegative() throws ParseException {
        Expression e = Parser.parse("(10 / (0 - 2))");
        assertEquals(-5, e.eval(""), "(10 / -2) должно быть -5");
    }

    @Test
    void testParseMissingParen() {
        assertThrows(ParseException.class, () -> Parser.parse("(3 + 2"),
                "Отсутствующая закрывающая скобка должна вызвать ParseException");
    }

    @Test
    void testParseEmptyInput() {
        assertThrows(ParseException.class, () -> Parser.parse(""),
                "Пустая строка должна вызвать ParseException");
    }

    @Test
    void testParseUnexpectedSymbol() {
        assertThrows(ParseException.class, () -> Parser.parse("(3 $ 5)"),
                "Недопустимый символ должен вызвать ParseException");
    }

    @Test
    void testParseStartsWithOperator() {
        assertThrows(ParseException.class, () -> Parser.parse("+ 5"),
                "Выражение не должно начинаться с оператора");
    }

    @Test
    void testParseEndsWithOperator() {
        assertThrows(ParseException.class, () -> Parser.parse("(3 + )"),
                "Выражение не должно заканчиваться оператором");
    }

    @Test
    void testLargeNumbers() throws ParseException {
        Expression e = Parser.parse("(1000000 * 1000)");
        assertEquals(1000000000, e.eval(""), "1e6 * 1e3 = 1e9");
    }

    @Test
    void testComplexMix() throws ParseException {
        Expression e = Parser.parse("(((x + 2) * (y - 3)) / (2 + 1))");
        assertEquals(6, e.eval("x = 4; y = 6"), "(((x + 2) * (y - 3)) / 3) при x=4, y=6 будет 6");
    }

    @Test
    void testDivisionChain() throws ParseException {
        Expression e = Parser.parse("((20 / 4) / 5)");
        assertEquals(1, e.eval(""), "((20 / 4) / 5) должно быть 1");
    }
}
