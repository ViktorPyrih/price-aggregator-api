package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductsResponseMapper {

    default ProductsResponse convertToResponse(List<String> productLinks,
                                               List<String> productImages,
                                               List<String> productPrices,
                                               List<String> productDescriptions,
                                               List<String> productTitles,
                                               int pagesCount) {
        int productsCount = min(productLinks.size(), productImages.size(), productPrices.size(), productDescriptions.size(), productTitles.size());

        return ProductsResponse.builder()
                .products(IntStream.range(0, productsCount)
                        .mapToObj(i -> ProductsResponse.Product.builder()
                                .link(productLinks.get(i))
                                .image(productImages.get(i))
                                .price(productPrices.get(i))
                                .description(productDescriptions.get(i))
                                .title(productTitles.get(i))
                                .build())
                        .toList())
                .pagesCount(pagesCount)
                .build();
    }

    default ProductsResponse convertToResponse(List<String> productLinks,
                                               List<String> productImages,
                                               List<String> productPrices,
                                               List<String> productDescriptions,
                                               List<String> productTitles) {
        return convertToResponse(productLinks, productImages, productPrices, productDescriptions, productTitles, 1);
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min()
                .orElseThrow();
    }
}
