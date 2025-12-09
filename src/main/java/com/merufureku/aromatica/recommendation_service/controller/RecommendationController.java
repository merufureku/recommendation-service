package com.merufureku.aromatica.recommendation_service.controller;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CBFResponse;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationController {

    private final IRecommendationService recommendationService;

    public RecommendationController(IRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendations/cbf")
    @Operation(summary = "Get content-based recommendations for a user")
    public ResponseEntity<BaseResponse<CBFResponse>> getContentBasedRecommendations(@RequestParam(name = "limit", defaultValue = "10") int limit,
                                                                                    @RequestParam(name = "version", required = false, defaultValue = "1") int version,
                                                                                    @RequestParam(name = "correlationId", required = false, defaultValue = "") String correlationId) {

        var baseParam = new BaseParam(version, correlationId);
        var response = recommendationService.getCBFRecommendations(getUserId(), limit, baseParam);

        return ResponseEntity.ok(response);
    }

    private Integer getUserId(){

        return (Integer) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
