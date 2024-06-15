package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductsResponseMapper {

    default ProductsResponse convertToResponse(String marketplace,
                                               List<String> productLinks,
                                               List<String> productImages,
                                               List<String> productPriceImages,
                                               List<String> productDescriptionImages,
                                               List<String> productTitles,
                                               List<String> productPrices,
                                               int pagesCount) {
        int productsCount = max(productLinks.size(), productImages.size(), productPriceImages.size(), productDescriptionImages.size(), productTitles.size(), productPrices.size());

        return ProductsResponse.builder()
                .products(IntStream.range(0, productsCount)
                        .mapToObj(i -> ProductsResponse.Product.builder()
                                .marketplace(marketplace)
                                .link(get(productLinks, i))
                                .image(get(productImages, i))
                                .priceImage(get(productPriceImages, i))
                                .descriptionImage(get(productDescriptionImages, i))
                                .title(get(productTitles, i))
                                .price(get(productPrices, i))
                                .build())
                        .toList())
                .pagesCount(pagesCount)
                .build();
    }

    default ProductsResponse convertToResponse(String marketplace,
                                               List<String> productLinks,
                                               List<String> productImages,
                                               List<String> productPriceImages,
                                               List<String> productDescriptionImages,
                                               List<String> productTitles,
                                               List<String> productPrices) {
        return convertToResponse(marketplace, productLinks, productImages, productPriceImages, productDescriptionImages, productTitles, productPrices, 1);
    }

    private static String get(List<String> list, int index) {
        if (index >= list.size()) {
            return null;
        }

        return list.get(index);
    }

    private static int max(int... numbers) {
        return Arrays.stream(numbers)
                .max()
                .orElseThrow();
    }
}
