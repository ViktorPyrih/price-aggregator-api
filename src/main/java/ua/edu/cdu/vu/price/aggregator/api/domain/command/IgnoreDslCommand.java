package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebElement;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.WebDriverRunner.driver;
import static java.util.Objects.isNull;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class IgnoreDslCommand extends DslCommand<ElementsCollection, ElementsCollection> {

    private final Set<Integer> indexes;

    @Override
    public ElementsCollection execute(String url, ElementsCollection input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("IGNORE command executed on null input");
        }

        List<WebElement> remainingElements = new LinkedList<>();
        for (int i = 0; i < input.size(); i++) {
            if (!indexes.contains(i)) {
                remainingElements.add(input.get(i));
            }
        }

        return new ElementsCollection(driver(), remainingElements);
    }
}
