package ua.edu.cdu.vu.price.aggregator.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DslEvaluationResponse {

    List<Object> values;

    @SuppressWarnings("unchecked")
    public <T> T getSingleValue() {
        return (T) values.getFirst();
    }
}
