package ua.edu.cdu.vu.price.aggregator.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.cdu.vu.price.aggregator.api.dto.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.FiltersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/categories")
public class FiltersController {

    private final FiltersService filtersService;

    @GetMapping("{category}/categories/{subcategory1}/categories/{subcategory2}/filters")
    public FiltersResponse getFilters(@PathVariable String marketplace, @PathVariable String category, @PathVariable String subcategory1, @PathVariable String subcategory2) {
        return filtersService.getFilters(marketplace, category, subcategory1, subcategory2);
    }
}
