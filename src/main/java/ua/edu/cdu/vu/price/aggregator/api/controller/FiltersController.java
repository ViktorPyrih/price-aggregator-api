package ua.edu.cdu.vu.price.aggregator.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.FiltersService;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeySecuritySchema;

@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/filters")
public class FiltersController {

    private final FiltersService filtersService;

    @ApiKeyRequired
    @GetMapping
    public FiltersResponse getFilters(@PathVariable String marketplace, @RequestParam String category, @RequestParam String subcategory1, @RequestParam String subcategory2) {
        return filtersService.getFilters(marketplace, category, subcategory1, subcategory2);
    }
}
