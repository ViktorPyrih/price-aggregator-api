package ua.edu.cdu.vu.price.aggregator.api.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.cdu.vu.price.aggregator.api.domain.ProductCategoryGenerator;
import ua.edu.cdu.vu.price.aggregator.api.dto.CategoryDto;
import ua.edu.cdu.vu.price.aggregator.api.mapper.CategoryMapper;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeySecuritySchema;

@RestController
@ApiKeySecuritySchema
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/category")
public class ProductCategoryController {

    private final ProductCategoryGenerator productCategoryGenerator;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @ApiKeyRequired
    public CategoryDto getCategory(@RequestParam String productName) {
        return categoryMapper.convertToDto(productCategoryGenerator.generate(productName));
    }
}
