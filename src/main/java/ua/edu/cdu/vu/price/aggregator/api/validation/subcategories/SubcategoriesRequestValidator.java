package ua.edu.cdu.vu.price.aggregator.api.validation.subcategories;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.service.MarketplacesService;
import ua.edu.cdu.vu.price.aggregator.api.validation.HttpRequestNotValidException;
import ua.edu.cdu.vu.price.aggregator.api.validation.HttpRequestValidator;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SubcategoriesRequestValidator implements HttpRequestValidator<String> {

    private static final String CATEGORY = "category";
    private static final String SUBCATEGORY = "subcategory";

    private final MarketplacesService marketplacesService;

    @Override
    public void validate(HttpServletRequest request, @NonNull String marketplace) throws HttpRequestNotValidException {
        int subcategoriesCount = marketplacesService.getSubcategoriesCount(marketplace);
        validate(request, subcategoriesCount);
    }

    private void validate(HttpServletRequest request, int subcategoriesCount) {
        if (Objects.isNull(request.getParameter(CATEGORY))) {
            throw new HttpRequestNotValidException("Category is required");
        }

        for (int i = 1; i <= subcategoriesCount; i++) {
            if (Objects.isNull(request.getParameter(SUBCATEGORY + i))) {
                throw new HttpRequestNotValidException("Subcategory %s is required".formatted(i));
            }
        }
    }
}
