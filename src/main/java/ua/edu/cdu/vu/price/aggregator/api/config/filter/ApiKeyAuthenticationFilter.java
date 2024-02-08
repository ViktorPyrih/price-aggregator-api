package ua.edu.cdu.vu.price.aggregator.api.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter implements Filter {

    public static final String API_KEY_HEADER_NAME = "x-api-key";
    private static final String API_KEY_IS_REQUIRED = "Api key is required";
    private static final String API_KEY_IS_NOT_VALID = "Api key is not valid";

    private static final Set<String> PATHS_TO_IGNORE = Set.of("/swagger-ui/.*", "/v3/api-docs.*");

    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String apiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if (shouldIgnoreUrl(servletRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String apiKeyHeader = servletRequest.getHeader(API_KEY_HEADER_NAME);
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (Objects.isNull(apiKeyHeader)) {
            servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            objectMapper.writeValue(response.getOutputStream(), ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, API_KEY_IS_REQUIRED));
            return;
        }
        if (!apiKeyHeader.equals(apiKey)) {
            servletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            objectMapper.writeValue(response.getOutputStream(), ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, API_KEY_IS_NOT_VALID));
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean shouldIgnoreUrl(String url) {
        return PATHS_TO_IGNORE.stream().anyMatch(url::matches);
    }
}
