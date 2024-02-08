package ua.edu.cdu.vu.price.aggregator.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.DslEvaluationService;
import ua.edu.cdu.vu.price.aggregator.api.swagger.ApiKeyRequired;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dsl/evaluation")
public class DslEvaluationController {

    private final DslEvaluationService dslEvaluationService;

    @ApiKeyRequired
    @RequestMapping(method = {GET, POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public DslEvaluationResponse<Object> evaluate(@RequestBody @Valid DslEvaluationRequest request) {
        return dslEvaluationService.evaluate(request);
    }

    @ApiKeyRequired
    @RequestMapping(path = "/image", method = {GET, POST}, produces = MediaType.IMAGE_PNG_VALUE)
    public FileSystemResource evaluateAsImage(@RequestBody @Valid DslEvaluationRequest request) {
        return dslEvaluationService.<FileSystemResource>evaluate(request).getValue();
    }
}
