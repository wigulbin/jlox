package com.craftinginterpreters.jlox;

import java.util.List;

class RpnPrinter implements Expr.Visitor<String>
{
    String print(Expr expr)
    {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr)
    {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr)
    {
        return parenthesize("", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr)
    {
        if(expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr)
    {
        return parenthesize(expr.operator.lexeme, expr.right);
    }


    private String parenthesize(String name, Expr... exprs)
    {
        StringBuilder builder = new StringBuilder();

        if(exprs.length == 1)
            builder.append(name);
        for (Expr expr : exprs)
        {
            builder.append(expr.accept(this));
            builder.append(" ");
        }
        if(exprs.length > 1)
            builder.append(name);

        return builder.toString();
    }

    public static void main(String[] args)
    {
        List<Expr> expressions = List.of(
                new Expr.Binary(
                        new Expr.Unary(
                                new Token(TokenType.MINUS, "-", null, 1),
                                new Expr.Literal(123)
                        ),
                        new Token(TokenType.STAR, "*", null, 1),
                        new Expr.Grouping(new Expr.Literal(45.67))
                ),
                new Expr.Binary(
                        new Expr.Binary(new Expr.Literal(1), new Token(TokenType.PLUS, "+", null, 1), new Expr.Literal(2)),
                        new Token(TokenType.STAR, "*", null, 1),
                        new Expr.Binary(new Expr.Literal(4), new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(3))
                )
        );

        for (Expr expression : expressions)
        {
            System.out.println(new AstPrinter().print(expression));
            System.out.println(new RpnPrinter().print(expression));
        }
    }
}
