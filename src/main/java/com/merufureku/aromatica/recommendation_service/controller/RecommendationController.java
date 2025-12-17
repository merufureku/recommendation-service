package com.merufureku.aromatica.recommendation_service.controller;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.RecommendationResponse;
import com.merufureku.aromatica.recommendation_service.services.factory.RecommendationServiceFactory;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationServiceFactory recommendationServiceFactory;

    public RecommendationController(RecommendationServiceFactory recommendationServiceFactory) {
        this.recommendationServiceFactory = recommendationServiceFactory;
    }

    @GetMapping("/cbf")
    @Operation(summary = "Get content-based recommendations for a user")
    public ResponseEntity<BaseResponse<RecommendationResponse>> getContentBasedRecommendations(@Valid @RequestParam(name = "limit", defaultValue = "10")
                                                                                               @Min(value = 1, message = "limit must be at least 1")
                                                                                               @Max(value = 10, message = "limit must be at most 10")
                                                                                               int limit,
                                                                                               @RequestParam(name = "version", required = false, defaultValue = "1") int version,
                                                                                               @RequestParam(name = "correlationId", required = false, defaultValue = "recommendation") String correlationId) {

        var baseParam = new BaseParam(version, correlationId);
        var response = recommendationServiceFactory.getService(version).
                getCBFRecommendations(getUserId(), limit, baseParam);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cf")
    @Operation(summary = "Get collaborative filtering recommendations for a user")
    public ResponseEntity<BaseResponse<RecommendationResponse>> getCollaborativeBasedRecommendations(@Valid @RequestParam(name = "limit", defaultValue = "10")
                                                                                                     @Min(value = 1, message = "limit must be at least 1")
                                                                                                     @Max(value = 10, message = "limit must be at most 10")
                                                                                                     int limit,
                                                                                                     @RequestParam(name = "version", required = false, defaultValue = "1") int version,
                                                                                                     @RequestParam(name = "correlationId", required = false, defaultValue = "recommendation") String correlationId) {

        var baseParam = new BaseParam(version, correlationId);
        var response = recommendationServiceFactory.getService(version)
                .getCFRecommendations(getUserId(), limit, baseParam);

        return ResponseEntity.ok(response);
    }

    private Integer getUserId(){

        return (Integer) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
