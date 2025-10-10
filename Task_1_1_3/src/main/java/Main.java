public class Main {
    public static void main(String[] args) throws ParseException {
        Expression e = Parser.parse(" (3 + (2 * x) ) ");
        System.out.print("e.print(): ");
        e.print();
        Expression de = e.derivative("x");
        System.out.print("de.print(): ");
        de.print();
        int val = e.eval("x = 10; y = 13");
        System.out.println("e.eval(\"x = 10; y = 13\") -> " + val);
    }
}
