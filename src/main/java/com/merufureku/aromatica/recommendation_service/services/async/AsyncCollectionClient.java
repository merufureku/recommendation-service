package com.merufureku.aromatica.recommendation_service.services.async;

import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.CollectionsResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.UserCollectionsResponse;
import com.merufureku.aromatica.recommendation_service.services.interfaces.ICollectionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncCollectionClient {

    private final ICollectionService collectionService;

    public AsyncCollectionClient(ICollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Async("recommendationExecutor")
    public CompletableFuture<UserCollectionsResponse> getUserCollection(int userId, int version, String correlationId){
        var response = collectionService.getUserCollections(userId, version, correlationId);
        return CompletableFuture.completedFuture(response.data());
    }

    @Async("recommendationExecutor")
    public CompletableFuture<CollectionsResponse> getAllCollectionsFromSimilarFragrance(Integer excludedUserId, GetFragranceBatchParam param, int version, String correlationId){
        var response = collectionService.getAllCollectionsFromSimilarFragrance(excludedUserId, param, version, correlationId);
        return CompletableFuture.completedFuture(response.data());
    }
}
