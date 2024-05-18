package ua.edu.cdu.vu.price.aggregator.api.domain;

import com.codeborne.selenide.ex.ElementNotFound;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.DslCommand;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.OpenDslCommand;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.StartDslCommand;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@ToString
@EqualsAndHashCode
@SuppressWarnings("rawtypes,unchecked")
public class DslExpression<T> implements Serializable {

    private static final int MAX_ATTEMPTS = 3;
    private static final String KEY_TEMPLATE = "'%s'";

    private final String expression;
    private final List<DslCommand> commands;

    public DslExpression(String expression) {
        this.expression = expression;
        this.commands = new LinkedList<>() {
            {
                add(OpenDslCommand.INSTANCE);
            }
        };
    }

    public T evaluate(String url, Map<String, Object> context, WebDriver webDriver) {
        return evaluate(url, context, webDriver, MAX_ATTEMPTS);
    }

    private T evaluate(String url, Map<String, Object> context, WebDriver webDriver, int attempts) {
        Object result = StartDslCommand.STUB;
        try {
            for (var command : commands) {
                log.debug("About to execute command: {}", command);
                result = command.execute(url, result, context, webDriver);
                log.debug("Command: {} executed successfully", command);
            }

            return (T) result;
        } catch (Throwable e) {
            if (shouldRetry(e) && attempts > 0) {
                log.error("Error occurred. Retrying DSL expression evaluation. Attempt: {}", attempts, e);
                return evaluate(url, context, webDriver, attempts - 1);
            }

            throw e;
        }
    }

    private boolean shouldRetry(Throwable e) {
        return e instanceof ElementNotFound;
    }

    public void addCommand(DslCommand command) {
        if (commands.size() == 1 && !(command instanceof StartDslCommand)) {
            throw new DslValidationException("Command: %s cannot be used as the first one".formatted(command.getClass().getSimpleName()));
        }
        commands.add(command);
    }

    public String toString(Map<String, Object> context) {
        return context.entrySet().stream()
                .reduce(expression, (expression, entry) -> expression.replace(KEY_TEMPLATE.formatted(entry.getKey()), entry.getValue().toString()),
                        (expression1, expression2) -> expression2);
    }
}
