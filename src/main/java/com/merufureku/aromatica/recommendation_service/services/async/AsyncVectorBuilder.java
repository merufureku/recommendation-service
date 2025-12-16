package com.merufureku.aromatica.recommendation_service.services.async;

import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import com.merufureku.aromatica.recommendation_service.helper.RecommendationHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.NO_FRAGRANCE_TO_RECOMMEND;
import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.NO_USER_COLLECTION;

@Service
public class AsyncVectorBuilder {

    private final RecommendationHelper recommendationHelper;

    public AsyncVectorBuilder(RecommendationHelper recommendationHelper) {
        this.recommendationHelper = recommendationHelper;
    }

    @Async("recommendationExecutor")
    public CompletableFuture<Map<Long, Float>> buildUserVector(List<FragranceNoteListResponse.FragranceNoteList> noteLists) {

        return CompletableFuture.completedFuture(recommendationHelper.getCollectionVector(noteLists))
                .thenApply(result -> {
                    if (result.isEmpty()) {
                        throw new ServiceException(NO_USER_COLLECTION);
                    }
                    return result;
                });
    }

    @Async("recommendationExecutor")
    public CompletableFuture<Map<Long, Map<Long, Float>>> buildAllPerfumeVectors(FragranceNoteListResponse response) {

        return CompletableFuture.completedFuture(recommendationHelper.getAllPerfumesVector(response))
                .thenApply(result -> {
                    if (result.isEmpty()) {
                        throw new ServiceException(NO_FRAGRANCE_TO_RECOMMEND);
                    }
                    return result;
                });
    }

}
