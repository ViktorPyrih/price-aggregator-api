package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;

public class ClickDslCommand extends CacheableDslCommand {

    @Override
    protected void action(SelenideElement element) {
        element.click();
    }
}
