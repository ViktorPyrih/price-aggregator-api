package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.WebDriverRunner.driver;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@EqualsAndHashCode(callSuper = true)
public class GroupByDslCommand extends DslCommand<ElementsCollection, List<ElementsCollection>> {

    private static final String PARENT_ID_MODE = "parent_id";

    public GroupByDslCommand(@NonNull String mode) {
        if (!PARENT_ID_MODE.equalsIgnoreCase(mode)) {
            throw new UnsupportedOperationException("Mode '%s' is not supported".formatted(mode));
        }
    }

    @Override
    public List<ElementsCollection> execute(String url, ElementsCollection input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("GROUP_BY command executed on null input");
        }

        return input.asFixedIterable().stream()
                .collect(Collectors.groupingBy(this::getFirstNotBlankParentId, LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(elements -> new ElementsCollection(driver(), elements))
                .toList();
    }

    private String getFirstNotBlankParentId(SelenideElement element) {
        SelenideElement parent = element.parent();
        while (isBlank(parent.getAttribute("id"))) {
            parent = parent.parent();
        }

        return parent.getAttribute("id");
    }
}
