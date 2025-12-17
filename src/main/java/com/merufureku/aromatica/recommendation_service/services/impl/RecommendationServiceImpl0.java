package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.RecommendationResponse;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import org.springframework.stereotype.Service;

@Service
public class RecommendationServiceImpl0 implements IRecommendationService {

    @Override
    public BaseResponse<RecommendationResponse> getCBFRecommendations(Integer userId, int limit, BaseParam baseParam) {
        return null;
    }

    @Override
    public BaseResponse<RecommendationResponse> getCFRecommendations(Integer userId, int limit, BaseParam baseParam) {
        return null;
    }
}
