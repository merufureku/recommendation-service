package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.RecommendationResponse;

public interface IRecommendationService {

    BaseResponse<RecommendationResponse> getCBFRecommendations(Integer userId, int limit, BaseParam baseParam);

    BaseResponse<RecommendationResponse> getCFRecommendations(Integer userId, int limit, BaseParam baseParam);

}
