package ua.edu.cdu.vu.price.aggregator.api.parser;

import com.codeborne.selenide.ElementsCollection;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.domain.command.*;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@Component
public class DslExpressionParser {

    private static final String DSL_COMMAND_SEPARATOR = "\\s?\\|\\s?";
    private static final String DSL_INTER_COMMAND_SEPARATOR = "\\s";
    private static final String DSL_ARGUMENTS_SEPARATOR = ",";
    private static final String DSL_ARGUMENT_SEPARATOR = "=";

    public <T> DslExpression<T> parse(@NonNull String selector) {
        return parse(selector, List.of());
    }

    public <T> DslExpression<T> parse(@NonNull String selector, List<String> otherSelectors) {
        otherSelectors = ofNullable(otherSelectors).orElse(List.of());
        DslExpression<T> dslExpression = new DslExpression<>();
        List<DslExpression<Object>> otherDslExpressions = parseOtherDslExpressions(otherSelectors);
        Arrays.stream(selector.split(DSL_COMMAND_SEPARATOR))
                .map(command -> command.split(DSL_INTER_COMMAND_SEPARATOR))
                .map(args -> parseCommand(args, otherDslExpressions))
                .forEach(dslExpression::addCommand);

        return dslExpression;
    }

    private List<DslExpression<Object>> parseOtherDslExpressions(List<String> otherSelectors) {
        List<String> filteredSelectors = otherSelectors;
        List<DslExpression<Object>> otherDslExpressions = new LinkedList<>();
        for (String otherSelector : otherSelectors) {
            filteredSelectors = filteredSelectors.stream()
                    .filter(not(otherSelector::equals))
                    .toList();
            otherDslExpressions.add(parse(otherSelector, filteredSelectors));
        }

        return otherDslExpressions;
    }

    @SuppressWarnings("unchecked,rawtypes")
    private DslCommand<?, ?> parseCommand(String[] args, List otherDslExpressions) {
        String command = args[0];
        var option = DslCommand.Option.parseOption(command);
        return switch (option) {
            case ANY -> new AnyDslCommand();
            case ATTRIBUTE -> createAttributeCommand(args);
            case BASE64 -> new Base64DslCommand();
            case BY_ID -> createByIdCommand(args);
            case BY_INDEX -> createByIndexCommand(args);
            case CLICK -> new ClickDslCommand();
            case DISTINCT -> new DistinctDslCommand();
            case FILTER -> createFilterCommand(args);
            case FIRST -> new FirstDslCommand();
            case GROUP_BY -> createGroupByCommand(args);
            case HOVER -> new HoverDslCommand();
            case IGNORE -> createIgnoreCommand(args);
            case INPUT -> createInputCommand(args);
            case JOIN -> createJoinCommand(args, otherDslExpressions);
            case SCREENSHOT -> new ScreenshotDslCommand();
            case SELECT -> createSelectCommand(args);
            case TEXT -> new TextDslCommand();
            case TO_LIST -> new ToListDslCommand();
            case TRIM -> new TrimDslCommand();
            case UNION -> createUnionCommand(args, otherDslExpressions);
            case WAIT -> createWaitCommand(args);
        };
    }

    private FilterDslCommand createFilterCommand(String[] args) {
        requireOneArgument(args);
        var argumentsMap = parseArgumentsAsMap(args[1]);

        return new FilterDslCommand(argumentsMap);
    }

    private IgnoreDslCommand createIgnoreCommand(String[] args) {
        requireOneArgument(args);
        try {
            return new IgnoreDslCommand(parseArgumentsAsSet(args[1]));
        } catch (NumberFormatException e) {
            throw new DslValidationException("'%s' cannot be converted to list of integers".formatted(args[1]));
        }
    }

    private SelectDslCommand createSelectCommand(String[] args) {
        requireOneArgument(args);
        return new SelectDslCommand(args[1]);
    }

    private ByIdDslCommand createByIdCommand(String[] args) {
        requireOneArgument(args);
        return new ByIdDslCommand(args[1]);
    }

    private GroupByDslCommand createGroupByCommand(String[] args) {
        requireOneArgument(args);
        return new GroupByDslCommand(args[1]);
    }

    private JoinDslCommand createJoinCommand(String[] args, List<DslExpression<Iterable<Object>>> otherDslExpressions) {
        requireOneArgument(args);
        return new JoinDslCommand(otherDslExpressions, parseArgumentsAsMap(args[1]));
    }

    private InputDslCommand createInputCommand(String[] args) {
        requireOneArgument(args);
        return new InputDslCommand(parseArgumentsAsMap(args[1]));
    }

    private AttributeDslCommand createAttributeCommand(String[] args) {
        requireOneArgument(args);
        return new AttributeDslCommand(args[1]);
    }

    private UnionDslCommand createUnionCommand(String[] args, List<DslExpression<ElementsCollection>> otherDslExpressions) {
        return new UnionDslCommand(otherDslExpressions, parseArgumentsAsMap(args[1]));
    }

    private WaitDslCommand createWaitCommand(String[] args) {
        requireOneArgument(args);
        return new WaitDslCommand(requireInteger(args[1]));
    }

    private ByIndexDslCommand createByIndexCommand(String[] args) {
        requireOneArgument(args);
        return new ByIndexDslCommand(requireInteger(args[1]));
    }

    private void requireOneArgument(String[] args) {
        if (args.length != 2) {
            throw new DslValidationException("%s command accepts exactly one argument".formatted(args[0]));
        }
    }

    private int requireInteger(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new DslValidationException("'%s' cannot be converted to integer".formatted(arg));
        }
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
