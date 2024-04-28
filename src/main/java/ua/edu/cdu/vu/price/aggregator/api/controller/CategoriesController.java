package ua.edu.cdu.vu.price.aggregator.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.CategoriesResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.CategoriesService;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeySecuritySchema;

@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    @ApiKeyRequired
    @GetMapping
    public CategoriesResponse getCategories(@PathVariable String marketplace) {
        return categoriesService.getCategories(marketplace);
    }

    @ApiKeyRequired
    @GetMapping(params = "category")
    public CategoriesResponse getSubcategories(@PathVariable String marketplace, @RequestParam String category) {
        return categoriesService.getCategories(marketplace, category);
    }

    @ApiKeyRequired
    @GetMapping(params = {"category", "subcategory"})
    public CategoriesResponse getSubcategories(@PathVariable String marketplace, @RequestParam String category, @RequestParam String subcategory) {
        return categoriesService.getCategories(marketplace, category, subcategory);
    }
}
