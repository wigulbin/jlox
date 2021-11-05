import java.util.ArrayList;
import java.util.List;

public class Scanner
{
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source)
    {
        this.source = source;
    }

    List<Token> scanTokens()
    {
        while (isAtEnd())
        {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken()
    {
        char c = advance();
        switch (c)
        {
            case '(' -> addToken(TokenType.LEFT_PAREN);
            case ')' -> addToken(TokenType.RIGHT_PAREN);
            case '{' -> addToken(TokenType.LEFT_BRACE);
            case '}' -> addToken(TokenType.RIGHT_BRACE);
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case '-' -> addToken(TokenType.MINUS);
            case '+' -> addToken(TokenType.PLUS);
            case ';' -> addToken(TokenType.SEMICOLON);
            case '*' -> addToken(TokenType.STAR);

            case '!' -> addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case '=' -> addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case '<' -> addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case '>' -> addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);

            case ' ', '\t', '\r' -> {}

            case '\n' -> line++;

            case '"' -> string();


            case '/' -> {
                if (match('/'))
                {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                else
                    addToken(TokenType.SLASH);
            }

            default -> {
                if(isDigit(c))
                    number();
                else
                    Lox.error(line, "Unexpected character.");
            }
        }
    }

    private boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    private void number()
    {
        while (isDigit(peek())) advance();

        if(peek() == '.' && isDigit(peekNext()))
        {
            advance();
            while(isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext()
    {
        if( current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void string()
    {
        while (peek() != '"' && !isAtEnd())
        {
            if(peek() == '\n') line++;
            advance();
        }

        if(isAtEnd())
        {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance(); // Closing "

        //Trim surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private char peek()
    {
        if (isAtEnd()) return '\0';

        return source.charAt(current);
    }

    private boolean match(char expected)
    {
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        //Expected does exist, we consume it and return true
        current++;
        return true;
    }

    private char advance()
    {
        return source.charAt(current++);
    }

    private void addToken(TokenType type)
    {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal)
    {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd()
    {
        return current >= source.length();
    }
}
