package ua.edu.cdu.vu.price.aggregator.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.FiltersService;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeySecuritySchema;
import ua.edu.cdu.vu.price.aggregator.api.util.RequestUtils;
import ua.edu.cdu.vu.price.aggregator.api.validation.subcategories.SubcategoriesRequestValidator;

@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/filters")
public class FiltersController {

    private final FiltersService filtersService;
    private final SubcategoriesRequestValidator subcategoriesRequestValidator;

    @ApiKeyRequired
    @GetMapping
    public FiltersResponse getFilters(HttpServletRequest request, @PathVariable String marketplace, @RequestParam String category) {
        subcategoriesRequestValidator.validate(request, marketplace);
        return filtersService.getFilters(marketplace, category, RequestUtils.extractSubcategories(request));
    }
}
