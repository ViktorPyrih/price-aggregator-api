package ua.edu.cdu.vu.price.aggregator.api.domain;

import com.codeborne.selenide.ex.ElementNotFound;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.DslCommand;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.OpenDslCommand;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.StartDslCommand;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@ToString
@EqualsAndHashCode
@SuppressWarnings("rawtypes,unchecked")
public class DslExpression<T> {

    private final List<DslCommand> commands;

    public DslExpression() {
        this.commands = new LinkedList<>() {
            {
                add(new OpenDslCommand());
            }
        };
    }

    public T evaluate(String url, Map<String, Object> context, WebDriver webDriver) {
        Object result = StartDslCommand.STUB;
        try {
            for (var command : commands) {
                result = command.execute(url, result, context, webDriver);
            }

            return (T) result;
        } catch (ClassCastException e) {
            throw new DslExecutionException(e);
        } catch (ElementNotFound e) {
            log.error("Element not found. Retrying DSL expression evaluation...", e);
            return evaluate(url, context, webDriver);
        }
    }

    public void addCommand(DslCommand command) {
        if (commands.size() == 1 && !(command instanceof StartDslCommand)) {
            throw new DslValidationException("Command: %s cannot be used as the first one".formatted(command.getClass().getSimpleName()));
        }
        commands.add(command);
    }
}
