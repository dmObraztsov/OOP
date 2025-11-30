package parser;

import atomic.Expression;
import atomic.Number;
import atomic.Variable;
import exceptions.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import operations.Add;
import operations.Div;
import operations.Mul;
import operations.Sub;


public class Parser {
    private enum TokType { NUMBER, IDENT, PLUS, MINUS, MUL, DIV, LPAREN, RPAREN, EOF }

    private static final Map<Character, TokType> SINGLE_CHAR_TOKENS = Map.of(
            '(', TokType.LPAREN,
            ')', TokType.RPAREN,
            '+', TokType.PLUS,
            '-', TokType.MINUS,
            '*', TokType.MUL,
            '/', TokType.DIV
    );


    private record Token(TokType type, String text) {

        public String toString() {
            return type + (text == null ? "" : "(" + text + ")");
        }
    }

    private final List<Token> tokens;
    private int pos = 0;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token cur() {
        return tokens.get(pos);
    }

    private Token move() {
        Token t = cur();
        pos = Math.min(pos + 1, tokens.size() - 1);
        return t;
    }

    public static Expression parse(String s) throws ParseException {
        List<Token> toks = lex(s);
        toks.add(new Token(TokType.EOF, null));
        Parser p = new Parser(toks);
        Expression e = p.parseExpression();
        if (p.cur().type != TokType.EOF) {
            throw new ParseException("Unexpected token after expression: " + p.cur());
        }
        return e;
    }

    private static int scanWhile(String s, int start,
                                 java.util.function.Predicate<Character> cond) {
        int i = start;
        while (i < s.length() && cond.test(s.charAt(i))) {
            i++;
        }
        return i;
    }

    private static List<Token> lex(String s) throws ParseException {
        List<Token> res = new ArrayList<>();
        int i = 0;
        int n = s.length();

        while (i < n) {
            char c = s.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            TokType type = SINGLE_CHAR_TOKENS.get(c);
            if (type != null) {
                res.add(new Token(type, String.valueOf(c)));
                i++;
                continue;
            }

            if (Character.isDigit(c)) {
                int j = scanWhile(s, i, Character::isDigit);
                res.add(new Token(TokType.NUMBER, s.substring(i, j)));
                i = j;
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                int j = scanWhile(s, i, ch -> Character.isLetterOrDigit(ch) || ch == '_');
                res.add(new Token(TokType.IDENT, s.substring(i, j)));
                i = j;
                continue;
            }

            throw new ParseException("Unexpected character: '" + c + "' at pos " + i);
        }

        return res;
    }

    private Expression parseExpression() throws ParseException {
        Token t = cur();
        if (t.type == TokType.NUMBER) {
            move();
            try {
                int v = Integer.parseInt(t.text);
                return new Number(v);
            } catch (NumberFormatException ex) {
                throw new ParseException("Bad integer: " + t.text);
            }
        } else if (t.type == TokType.IDENT) {
            move();
            return new Variable(t.text);
        } else if (t.type == TokType.LPAREN) {
            move();
            Expression left = parseExpression();
            Token op = cur();
            if (!(op.type == TokType.PLUS || op.type == TokType.MINUS
                    || op.type == TokType.MUL || op.type == TokType.DIV)) {
                throw new ParseException("Expected operator after "
                        + "left expression inside parentheses, found: " + op);
            }
            move();
            Expression right = parseExpression();
            if (cur().type != TokType.RPAREN) {
                throw new ParseException("Expected ')' but found: " + cur());
            }
            move();
            return switch (op.type) {
                case PLUS -> new Add(left, right);
                case MINUS -> new Sub(left, right);
                case MUL -> new Mul(left, right);
                case DIV -> new Div(left, right);
                default -> throw new ParseException("Unknown operator: " + op);
            };
        } else {
            throw new ParseException("Unexpected token while parsing expression: " + t);
        }
    }
}
