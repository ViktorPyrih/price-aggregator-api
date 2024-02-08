package ua.edu.cdu.vu.price.aggregator.api.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ua.edu.cdu.vu.price.aggregator.api.config.filter.ApiKeyAuthenticationFilter.API_KEY_HEADER_NAME;

@Parameter(name = API_KEY_HEADER_NAME, required = true, in = ParameterIn.HEADER)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiKeyRequired {
}
