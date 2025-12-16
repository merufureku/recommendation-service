package com.merufureku.aromatica.recommendation_service.services.async;

import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.GetAllReviews;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IReviewService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncReviewsClient {

    private final IReviewService reviewService;

    public AsyncReviewsClient(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Async("recommendationExecutor")
    public CompletableFuture<GetAllReviews> getUserReviews(int userId, int minRating, int version, String correlationId) {
        var response = reviewService.getUserReviews(userId, minRating, version, correlationId);
        return CompletableFuture.completedFuture(response.data());
    }

    @Async("recommendationExecutor")
    public CompletableFuture<GetAllReviews> getReviews(Integer excludedUserId, GetFragranceBatchParam param, int minRating, int version, String correlationId) {
        var response = reviewService.getReviews(excludedUserId, param, minRating, version, correlationId);
        return CompletableFuture.completedFuture(response.data());
    }
}
