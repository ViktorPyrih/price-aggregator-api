package ua.edu.cdu.vu.price.aggregator.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.cdu.vu.price.aggregator.api.dto.MarketplacesResponse;
import ua.edu.cdu.vu.price.aggregator.api.service.MarketplacesService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketplaces")
public class MarketplacesController {

    private final MarketplacesService marketplacesService;

    @GetMapping
    public MarketplacesResponse getMarketplaces() {
        return marketplacesService.getMarketplaces();
    }
}
