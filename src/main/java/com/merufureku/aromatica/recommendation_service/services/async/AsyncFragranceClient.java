package com.merufureku.aromatica.recommendation_service.services.async;

import com.merufureku.aromatica.recommendation_service.dto.params.ExcludeFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;
import com.merufureku.aromatica.recommendation_service.services.impl.FragranceService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IFragranceService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncFragranceClient {

    private final IFragranceService fragranceService;

    public AsyncFragranceClient(FragranceService fragranceService) {
        this.fragranceService = fragranceService;
    }

    @Async("recommendationExecutor")
    public CompletableFuture<FragranceNoteListResponse> getUserCollectionNotes(Set<Long> ids, String correlationId) {
        var result = fragranceService.getPerfumeNotes(new GetFragranceBatchParam(ids), 1, correlationId).data();
        return CompletableFuture.completedFuture(result);
    }

    @Async("recommendationExecutor")
    public CompletableFuture<FragranceNoteListResponse> getAllFragranceNotes(Set<Long> excludedIds, String correlationId) {
        var result = fragranceService.getPerfumeNotes(new ExcludeFragranceBatchParam(excludedIds), 1, correlationId).data();
        return CompletableFuture.completedFuture(result);
    }
}
