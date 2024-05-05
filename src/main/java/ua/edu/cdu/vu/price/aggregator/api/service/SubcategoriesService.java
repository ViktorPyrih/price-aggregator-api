package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static ua.edu.cdu.vu.price.aggregator.api.util.CommonConstants.CATEGORY;
import static ua.edu.cdu.vu.price.aggregator.api.util.CommonConstants.SUBCATEGORY;

@Service
@RequiredArgsConstructor
public class SubcategoriesService {

    private final MarketplacesService marketplacesService;

    @Value("${price-aggregator-api.config.subcategories.max-count}")
    private int maxSubcategoriesCount;

    public Map<String, Object> getSubcategoriesMap(String marketplace, String category, Map<String, String> subcategories) {
        int maxSubcategoriesLevelsCount = Math.min(maxSubcategoriesCount, marketplacesService.getSubcategoriesCount(marketplace));
        int subcategoriesLevelsCount = subcategories.size();

        Map<String, Object> arguments = new HashMap<>(Map.of(CATEGORY, category));
        for (int i = 1; i <= Math.min(maxSubcategoriesLevelsCount, subcategoriesLevelsCount); i++) {
            String subcategory = SUBCATEGORY + i;
            arguments.put(subcategory, subcategories.get(subcategory));
        }

        return arguments;
    }
}
