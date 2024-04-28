package ua.edu.cdu.vu.price.aggregator.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BooleanUtils {

    public static boolean tryParse(String value) {
        return Boolean.TRUE.toString().equalsIgnoreCase(value);
    }
}
