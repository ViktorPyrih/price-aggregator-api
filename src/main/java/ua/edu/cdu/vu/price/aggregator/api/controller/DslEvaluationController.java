package ua.edu.cdu.vu.price.aggregator.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.DslEvaluationService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dsl/evaluation")
public class DslEvaluationController {

    private final DslEvaluationService dslEvaluationService;

    @RequestMapping(method = {GET, POST})
    public DslEvaluationResponse evaluate(@RequestBody @Valid DslEvaluationRequest request) {
        return dslEvaluationService.evaluate(request);
    }
}
