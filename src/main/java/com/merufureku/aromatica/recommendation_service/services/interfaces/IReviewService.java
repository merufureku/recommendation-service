package com.merufureku.aromatica.recommendation_service.services.interfaces;

import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.GetAllReviews;

public interface IReviewService {

    BaseResponse<GetAllReviews> getUserReviews(int userId, int minRating, int version, String correlationId);

    BaseResponse<GetAllReviews> getReviews(Integer excludedUserId, GetFragranceBatchParam param, int minRating, int version, String correlationId);
}
