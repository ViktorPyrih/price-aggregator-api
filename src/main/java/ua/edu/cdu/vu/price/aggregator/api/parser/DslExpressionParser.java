package ua.edu.cdu.vu.price.aggregator.api.parser;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.*;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DslExpressionParser {

    private static final String DSL_COMMAND_SEPARATOR = "\\s?\\|\\s?";
    private static final String DSL_INTER_COMMAND_SEPARATOR = "\\s";
    private static final String DSL_ARGUMENTS_SEPARATOR = ",";
    private static final String DSL_ARGUMENT_SEPARATOR = "=";

    public <T> DslExpression<T> parse(@NonNull String selector) {
        return parse(selector, List.of());
    }

    public <T> DslExpression<T> parse(@NonNull String selector, @NonNull List<String> otherSelectors) {
        DslExpression<T> dslExpression = new DslExpression<>();
        List<DslExpression<Iterable<Object>>> otherDslExpressions = otherSelectors.stream()
                .map(this::<Iterable<Object>>parse)
                .toList();
        Arrays.stream(selector.split(DSL_COMMAND_SEPARATOR))
                .map(command -> command.split(DSL_INTER_COMMAND_SEPARATOR))
                .map(args -> parseCommand(args, otherDslExpressions))
                .forEach(dslExpression::addCommand);

        return dslExpression;
    }

    private DslCommand<?, ?> parseCommand(String[] args, List<DslExpression<Iterable<Object>>> otherDslExpressions) {
        String command = args[0];
        var option = DslCommand.Option.parseOption(command);
        return switch (option) {
            case BY_ID -> createByIdCommand(args);
            case CLICK -> new ClickDslCommand();
            case FILTER -> createFilterCommand(args);
            case FIRST -> new FirstDslCommand();
            case GROUP_BY -> createGroupByCommand(args);
            case HOVER -> new HoverDslCommand();
            case IGNORE -> createIgnoreCommand(args);
            case SCREENSHOT -> new ScreenshotDslCommand();
            case SELECT -> createSelectCommand(args);
            case TEXT -> new TextDslCommand();
            case ZIP -> createZipCommand(args, otherDslExpressions);
        };
    }

    private FilterDslCommand createFilterCommand(String[] args) {
        if (args.length != 2) {
            throw new DslValidationException("FILTER command accepts exactly one argument");
        }
        var argumentsMap = parseArgumentsAsMap(args[1]);

        return new FilterDslCommand(argumentsMap);
    }

    private IgnoreDslCommand createIgnoreCommand(String[] args) {
        if (args.length != 2) {
            throw new DslValidationException("IGNORE command accepts exactly one argument");
        }

        try {
            return new IgnoreDslCommand(parseArgumentsAsSet(args[1]));
        } catch (NumberFormatException e) {
            throw new DslValidationException("'%s' cannot be converted to list of integers".formatted(args[1]));
        }
    }

    private SelectDslCommand createSelectCommand(String[] args) {
        if (args.length != 2) {
            throw new DslValidationException("SELECT command accepts exactly one argument");
        }

        return new SelectDslCommand(args[1]);
    }

    private ByIdDslCommand createByIdCommand(String[] args) {
        if (args.length != 2) {
            throw new DslValidationException("BY_ID command accepts exactly one argument");
        }

        return new ByIdDslCommand(args[1]);
    }

    private GroupByDslCommand createGroupByCommand(String[] args) {
        if (args.length != 2) {
            throw new DslValidationException("GROUP_BY command accepts exactly one argument");
        }

        return new GroupByDslCommand(args[1]);
    }

    private ZipDslCommand createZipCommand(String[] args, List<DslExpression<Iterable<Object>>> otherDslExpressions) {
        if (args.length != 2) {
            throw new DslValidationException("ZIP command accepts exactly one argument");
        }

        return new ZipDslCommand(otherDslExpressions, parseArgumentsAsMap(args[1]));
    }

    private Set<Integer> parseArgumentsAsSet(String arguments) {
        return Arrays.stream(arguments.split(DSL_ARGUMENTS_SEPARATOR))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    private Map<String, String> parseArgumentsAsMap(String arguments) {
        return Arrays.stream(arguments.split(DSL_ARGUMENTS_SEPARATOR))
                .map(argument -> argument.split(DSL_ARGUMENT_SEPARATOR))
                .collect(Collectors.toMap(argument -> argument[0], argument -> argument[1], (v1, v2) -> v2));
    }
}
