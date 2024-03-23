package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.conditions.Visible;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class VisibleDslCommand extends DslCommand<ElementsCollection, ElementsCollection> {

    public static final VisibleDslCommand INSTANCE = new VisibleDslCommand();

    @Override
    ElementsCollection executeInternal(String url, ElementsCollection input, Map<String, Object> context, WebDriver webDriver) {
        return input.filter(new Visible());
    }
}
