package com.merufureku.aromatica.recommendation_service.services.factory;

import com.merufureku.aromatica.recommendation_service.services.impl.RecommendationServiceImpl0;
import com.merufureku.aromatica.recommendation_service.services.impl.RecommendationServiceImpl1;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import org.springframework.stereotype.Component;

@Component
public class RecommendationServiceFactory {

    private final RecommendationServiceImpl0 recommendationServiceImpl0;
    private final RecommendationServiceImpl1 recommendationServiceImpl1;

    public RecommendationServiceFactory(RecommendationServiceImpl0 recommendationServiceImpl0, RecommendationServiceImpl1 recommendationServiceImpl1) {
        this.recommendationServiceImpl0 = recommendationServiceImpl0;
        this.recommendationServiceImpl1 = recommendationServiceImpl1;
    }

    public IRecommendationService getService(int version) {
        return switch (version) {
            case 0 -> recommendationServiceImpl0;
            case 1 -> recommendationServiceImpl1;
            default -> throw new IllegalArgumentException("Unsupported recommendation service version: " + version);
        };
    }
}
