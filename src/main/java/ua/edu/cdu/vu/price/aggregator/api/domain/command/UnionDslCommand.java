package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.codeborne.selenide.WebDriverRunner.driver;

@EqualsAndHashCode(callSuper = true)
public class UnionDslCommand extends CompositeDslCommand<ElementsCollection, ElementsCollection> {

    public UnionDslCommand(@NonNull List<DslExpression<ElementsCollection>> otherDslExpressions, @NonNull Map<String, String> arguments) {
        super(otherDslExpressions, arguments);
    }

    @Override
    protected ElementsCollection combine(ElementsCollection input1, ElementsCollection input2) {
        var elements = Stream.concat(input1.asFixedIterable().stream(), input2.asFixedIterable().stream()).toList();
        return new ElementsCollection(driver(), elements);
    }
}
