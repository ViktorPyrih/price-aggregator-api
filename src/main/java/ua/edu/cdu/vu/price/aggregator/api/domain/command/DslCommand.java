package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import org.openqa.selenium.Dimension;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.WebDriverRunner.driver;

public abstract class DslCommand<IN, OUT> {

    public enum Option {

        ATTRIBUTE, BASE64, BY_ID, CLICK, FILTER, FIRST, GROUP_BY, HOVER, IGNORE, INPUT, JOIN, SELECT, TEXT, SCREENSHOT;

        public static Option parseOption(String name) {
            return Arrays.stream(values())
                    .filter(option -> option.name().equals(name))
                    .findAny()
                    .orElseThrow(() -> new UnsupportedOperationException("Command '%s' is not supported".formatted(name)));
        }
    }

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final ParserContext PARSER_CONTEXT = new TemplateParserContext("$(", ")");

    public abstract OUT execute(String url, IN input, Map<String, Object> context);

    protected String parse(String expression, Map<String, Object> context) {
        return PARSER.parseExpression(expression, PARSER_CONTEXT).getValue(context, String.class);
    }

    protected int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new DslValidationException("%s cannot be converted to integer".formatted(value));
        }
    }

    protected void resizeWindow() {
        driver().getWebDriver().manage().window().setSize(new Dimension(2048, 2048));
    }
}
