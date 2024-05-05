package ua.edu.cdu.vu.price.aggregator.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.CategoriesResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.CategoriesService;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeySecuritySchema;
import ua.edu.cdu.vu.price.aggregator.api.util.RequestUtils;
import ua.edu.cdu.vu.price.aggregator.api.validation.subcategories.SubcategoriesCountRequestValidator;

@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}")
public class CategoriesController {

    private final CategoriesService categoriesService;
    private final SubcategoriesCountRequestValidator subcategoriesCountRequestValidator;

    @ApiKeyRequired
    @GetMapping("/categories")
    public CategoriesResponse getCategories(@PathVariable String marketplace) {
        return categoriesService.getCategories(marketplace);
    }

    @ApiKeyRequired
    @GetMapping("/subcategories")
    public CategoriesResponse getSubcategories(HttpServletRequest request, @PathVariable String marketplace, @RequestParam String category) {
        subcategoriesCountRequestValidator.validate(request);
        return categoriesService.getSubcategories(marketplace, category, RequestUtils.extractSubcategories(request));
    }
}
