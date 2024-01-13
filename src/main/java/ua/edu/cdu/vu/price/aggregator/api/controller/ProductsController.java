package ua.edu.cdu.vu.price.aggregator.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.ProductsService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/categories/{category}/categories/{subcategory1}/categories/{subcategory2}/products")
public class ProductsController {

    protected final ProductsService productsService;

    @RequestMapping(method = {GET, POST})
    public ProductsResponse getFilters(@PathVariable String marketplace,
                                       @PathVariable String category,
                                       @PathVariable String subcategory1,
                                       @PathVariable String subcategory2,
                                       @RequestBody ProductsRequest productsRequest) {
        return productsService.getProducts(marketplace, category, subcategory1, subcategory2, productsRequest);
    }
}
