package ua.edu.cdu.vu.price.aggregator.api.domain;

import ua.edu.cdu.vu.price.aggregator.api.domain.command.DslCommand;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.OpenDslCommand;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes,unchecked")
public class DslExpression<T> {

    private final List<DslCommand> commands;

    public DslExpression() {
        this.commands = new ArrayList<>() {
            {
                add(new OpenDslCommand());
            }
        };
    }

    public T evaluate(String url, Map<String, String> context) {
        Object result = null;
        try {
            for (var command : commands) {
                result = command.execute(url, result, context);
            }

            return (T) result;
        } catch (ClassCastException e) {
            throw new DslExecutionException(e);
        }
    }

    public void addCommand(DslCommand command) {
        commands.add(command);
    }
}
