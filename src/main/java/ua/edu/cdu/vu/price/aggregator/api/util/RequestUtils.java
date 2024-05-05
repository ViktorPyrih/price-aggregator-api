package ua.edu.cdu.vu.price.aggregator.api.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.stream.Collectors;

import static ua.edu.cdu.vu.price.aggregator.api.util.CommonConstants.SUBCATEGORY;

@UtilityClass
public class RequestUtils {

    public static Map<String, String> extractSubcategories(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(SUBCATEGORY))
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()[0]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
