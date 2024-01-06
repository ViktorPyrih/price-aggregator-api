package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Arrays;
import java.util.Map;

public abstract class DslCommand<IN, OUT> {

    public enum Option {

        CLICK, FILTER, FIRST, HOVER, IGNORE, SELECT, TEXT;

        public static Option parseOption(String name) {
            return Arrays.stream(values())
                    .filter(option -> option.name().equals(name))
                    .findAny()
                    .orElseThrow(() -> new UnsupportedOperationException("Command '%s' is not supported".formatted(name)));
        }
    }

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final ParserContext PARSER_CONTEXT = new TemplateParserContext("$(", ")");

    public abstract OUT execute(String url, IN input, Map<String, String> context);

    String parse(String expression, Map<String, String> context) {
        return PARSER.parseExpression(expression, PARSER_CONTEXT).getValue(context, String.class);
    }
}
