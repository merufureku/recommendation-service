package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CBFResponse;

public interface IRecommendationService {

    BaseResponse<CBFResponse> getCBFRecommendations(int limit, BaseParam baseParam);

}
