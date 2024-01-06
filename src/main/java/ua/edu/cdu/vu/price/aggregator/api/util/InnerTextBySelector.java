package ua.edu.cdu.vu.price.aggregator.api.util;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import lombok.NonNull;
import org.openqa.selenium.WebElement;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;

public class InnerTextBySelector extends WebElementCondition {

    private final String selector;
    private final String innerText;

    public InnerTextBySelector(String selector, String innerText) {
        super(InnerTextBySelector.class.getSimpleName());
        this.selector = selector;
        this.innerText = innerText;
    }

    @NonNull
    @Override
    public CheckResult check(Driver driver, WebElement element) {
        String elementInnerText = $(element).$(selector).innerText();
        if (Objects.equals(elementInnerText, innerText)) {
            return CheckResult.accepted(elementInnerText);
        }

        return CheckResult.rejected("Inner text doesn't match: '%s' by selector: '%s'".formatted(innerText, selector), innerText);
    }
}
