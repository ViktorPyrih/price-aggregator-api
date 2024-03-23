package ua.edu.cdu.vu.price.aggregator.api.util;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class InnerTextBySelector extends WebElementCondition {

    private static final String NAME_TEMPLATE = "%s selector=%s,text=%s";
    private static final String CONTROL_CHARS_REGEX = "\\p{Cntrl}";
    private static final String NBSP = "\u00A0";

    private final String selector;
    private final String innerText;

    public InnerTextBySelector(String selector, String innerText) {
        super(NAME_TEMPLATE.formatted(InnerTextBySelector.class.getSimpleName(), selector, innerText));
        this.selector = selector;
        this.innerText = innerText;
    }

    @NonNull
    @Override
    public CheckResult check(Driver driver, WebElement element) {
        try {
            String elementInnerText = prepare($(element).$(selector).innerText());
            if (elementInnerText.equals(innerText)) {
                return CheckResult.accepted(elementInnerText);
            }
        } catch (ElementNotFound e) {
            log.warn("Element not found by selector: '{}' in element: {}", selector, element);
        }

        return CheckResult.rejected("Inner text doesn't match: '%s' by selector: '%s'".formatted(innerText, selector), innerText);
    }

    private String prepare(String string) {
        return string.trim()
                .replaceAll(CONTROL_CHARS_REGEX, StringUtils.EMPTY)
                .replace(NBSP, StringUtils.EMPTY);
    }
}
