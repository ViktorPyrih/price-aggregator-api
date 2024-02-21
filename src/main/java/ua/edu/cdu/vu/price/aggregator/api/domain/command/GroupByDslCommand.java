package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.WebDriverRunner.driver;
import static org.apache.commons.lang3.StringUtils.isBlank;

@EqualsAndHashCode(callSuper = true)
public class GroupByDslCommand extends DslCommand<ElementsCollection, List<ElementsCollection>> {

    @Getter
    @RequiredArgsConstructor
    private enum Mode {

        PARENT_ID {

            private static final String ID = "id";

            @Override
            Object extractKey(SelenideElement element) {
                SelenideElement parent = element.parent();
                while (isBlank(parent.getAttribute(ID))) {
                    parent = parent.parent();
                }

                return parent.getAttribute(ID);
            }
        }, PARENT {

            @Override
            Object extractKey(SelenideElement element) {
                return element.parent();
            }
        };

        abstract Object extractKey(SelenideElement element);

        private static Mode parseMode(String mode) {
            return Arrays.stream(values())
                    .filter(m -> m.name().equalsIgnoreCase(mode))
                    .findAny()
                    .orElseThrow(() -> new UnsupportedOperationException("Mode '%s' is not supported".formatted(mode)));
        }
    }

    private final Mode mode;

    public GroupByDslCommand(@NonNull String mode) {
        this.mode = Mode.parseMode(mode);
    }

    @Override
    public List<ElementsCollection> executeInternal(String url, ElementsCollection input, Map<String, Object> context) {
        return input.asFixedIterable().stream()
                .collect(Collectors.groupingBy(mode::extractKey, LinkedHashMap::new, Collectors.toUnmodifiableList()))
                .values().stream()
                .map(elements -> new ElementsCollection(driver(), elements))
                .toList();
    }
}
