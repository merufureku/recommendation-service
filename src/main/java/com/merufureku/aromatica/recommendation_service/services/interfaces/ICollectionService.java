package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CollectionsResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.UserCollectionsResponse;

public interface ICollectionService {

    BaseResponse<UserCollectionsResponse> getUserCollections(Integer userId, int version, String correlationId);

    BaseResponse<CollectionsResponse> getAllCollectionsFromSimilarFragrance(Integer excludedUserId, GetFragranceBatchParam param, int version, String correlationId);
}
