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
                                               List<String> productPriceImages,
                                               List<String> productDescriptionImages,
                                               List<String> productTitles,
                                               List<String> productPrices,
                                               int pagesCount) {
        int productsCount = min(productLinks.size(), productImages.size(), productPriceImages.size(), productDescriptionImages.size(), productTitles.size(), productPrices.size());

        return ProductsResponse.builder()
                .products(IntStream.range(0, productsCount)
                        .mapToObj(i -> ProductsResponse.Product.builder()
                                .link(productLinks.get(i))
                                .image(productImages.get(i))
                                .priceImage(productPriceImages.get(i))
                                .descriptionImage(productDescriptionImages.get(i))
                                .title(productTitles.get(i))
                                .price(productPrices.get(i))
                                .build())
                        .toList())
                .pagesCount(pagesCount)
                .build();
    }

    default ProductsResponse convertToResponse(List<String> productLinks,
                                               List<String> productImages,
                                               List<String> productPriceImages,
                                               List<String> productDescriptionImages,
                                               List<String> productTitles,
                                               List<String> productPrices) {
        return convertToResponse(productLinks, productImages, productPriceImages, productDescriptionImages, productTitles, productPrices, 1);
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min()
                .orElseThrow();
    }
}
