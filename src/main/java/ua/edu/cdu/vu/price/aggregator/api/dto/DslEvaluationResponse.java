package ua.edu.cdu.vu.price.aggregator.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DslEvaluationResponse<T> {

    T value;

}
