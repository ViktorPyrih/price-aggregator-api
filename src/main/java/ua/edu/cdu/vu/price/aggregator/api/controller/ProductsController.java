package ua.edu.cdu.vu.price.aggregator.api.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import ua.edu.cdu.vu.price.aggregator.api.util.RequestUtils;
import ua.edu.cdu.vu.price.aggregator.api.validation.subcategories.SubcategoriesRequestValidator;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces/{marketplace}/products")
public class ProductsController {

    private final ProductsService productsService;
    private final SubcategoriesRequestValidator subcategoriesRequestValidator;

    @ApiKeyRequired
    @RequestMapping(method = {GET, POST})
    public ProductsResponse getProducts(HttpServletRequest request,
                                        @PathVariable String marketplace,
                                        @RequestParam String category,
                                        @RequestBody @Valid ProductsRequest productsRequest,
                                        @RequestParam(required = false, defaultValue = "1") @Positive int page) {
        subcategoriesRequestValidator.validate(request, marketplace);
        return productsService.getProducts(marketplace, category, RequestUtils.extractSubcategories(request), productsRequest, page);
    }
}
