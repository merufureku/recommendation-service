package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CollectionsResponse;

public interface ICollectionService {

    BaseResponse<CollectionsResponse> getUserCollections(Integer userId, int version, String correlationId);

}
