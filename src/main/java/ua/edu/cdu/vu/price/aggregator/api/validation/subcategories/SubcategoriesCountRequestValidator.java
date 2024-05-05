package ua.edu.cdu.vu.price.aggregator.api.validation.subcategories;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.util.RequestUtils;
import ua.edu.cdu.vu.price.aggregator.api.validation.HttpRequestNotValidException;
import ua.edu.cdu.vu.price.aggregator.api.validation.HttpRequestValidator;

@Component
public class SubcategoriesCountRequestValidator implements HttpRequestValidator<Void> {

    private static final String ERROR_MESSAGE = "Subcategories count should be less than or equal to %d";

    @Value("${price-aggregator-api.config.subcategories.max-count}")
    private int maxCount;

    @Override
    public void validate(HttpServletRequest request, Void context) throws HttpRequestNotValidException {
        var subcategories = RequestUtils.extractSubcategories(request);
        if (subcategories.size() > maxCount - 1) {
            throw new HttpRequestNotValidException(ERROR_MESSAGE.formatted(maxCount));
        }
    }
}
