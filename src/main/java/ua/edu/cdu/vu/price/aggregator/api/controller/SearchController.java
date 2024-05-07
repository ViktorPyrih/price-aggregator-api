package ua.edu.cdu.vu.price.aggregator.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.ProductsService;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeySecuritySchema;

@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SearchController {

    private final ProductsService productsService;

    @GetMapping("/search")
    @ApiKeyRequired
    public ProductsResponse search(@RequestParam String query) {
        return productsService.search(query);
    }

    @GetMapping("/{marketplace}/search")
    @ApiKeyRequired
    public ProductsResponse search(@PathVariable String marketplace, @RequestParam String query) {
        return productsService.search(marketplace, query);
    }
}
