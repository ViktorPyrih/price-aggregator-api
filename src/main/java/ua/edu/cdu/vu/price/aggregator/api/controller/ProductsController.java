package ua.edu.cdu.vu.price.aggregator.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.ProductsService;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeySecuritySchema;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/categories/{category}/categories/{subcategory1}/categories/{subcategory2}/products")
public class ProductsController {

    protected final ProductsService productsService;

    @ApiKeyRequired
    @RequestMapping(method = {GET, POST})
    public ProductsResponse getFilters(@PathVariable String marketplace,
                                       @PathVariable String category,
                                       @PathVariable String subcategory1,
                                       @PathVariable String subcategory2,
                                       @RequestBody @Valid ProductsRequest productsRequest,
                                       @RequestParam(required = false, defaultValue = "1") @Positive int page) {
        return productsService.getProducts(marketplace, category, subcategory1, subcategory2, productsRequest, page);
    }
}
