package ua.edu.cdu.vu.price.aggregator.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.cdu.vu.price.aggregator.api.dto.CategoriesResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.CategoriesService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping
    public CategoriesResponse getCategories(@PathVariable String marketplace) {
        return CategoriesResponse.builder()
                .categories(categoriesService.getCategories(marketplace))
                .build();
    }

    @GetMapping("{category}/categories")
    public CategoriesResponse getCategories(@PathVariable String marketplace, @PathVariable String category) {
        return CategoriesResponse.builder()
                .categories(categoriesService.getCategories(marketplace, category))
                .build();
    }

    @GetMapping("{category}/categories/{subcategory}/categories")
    public CategoriesResponse getSubcategories(@PathVariable String marketplace, @PathVariable String category, @PathVariable String subcategory) {
        return CategoriesResponse.builder()
                .categories(categoriesService.getCategories(marketplace, category, subcategory))
                .build();
    }
}
