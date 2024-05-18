package ua.edu.cdu.vu.price.aggregator.api.ai;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.OutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.domain.Category;
import ua.edu.cdu.vu.price.aggregator.api.domain.ProductCategoryGenerator;

@Component
@RateLimiter(name = "openai")
@RequiredArgsConstructor
public class AiProductCategoryGenerator implements ProductCategoryGenerator {

    private static final String PRODUCT_NAME = "productName";
    private static final String FORMAT = "format";

    private final ChatClient chatClient;
    private final OutputParser<Category> outputParser;

    @Value("classpath:prompts/category-by-product-name.txt")
    private Resource prompt;

    @Override
    @Cacheable("product-category")
    public Category generate(String productName) {
        PromptTemplate template = new PromptTemplate(prompt);
        template.setOutputParser(outputParser);
        template.add(PRODUCT_NAME, productName);
        template.add(FORMAT, outputParser.getFormat());

        String content = chatClient.call(template.create()).getResult().getOutput().getContent();

        return outputParser.parse(content);
    }
}
