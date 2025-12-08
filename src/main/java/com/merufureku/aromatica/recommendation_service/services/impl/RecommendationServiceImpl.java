package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.params.FragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CBFResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CollectionsResponse;
import com.merufureku.aromatica.recommendation_service.helper.RecommendationHelper;
import com.merufureku.aromatica.recommendation_service.services.interfaces.ICollectionService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IFragranceService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RecommendationServiceImpl implements IRecommendationService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final ICollectionService collectionsService;
    private final IFragranceService fragranceService;
    private final RecommendationHelper recommendationHelper;

    public RecommendationServiceImpl(ICollectionService collectionsService, IFragranceService fragranceService, RecommendationHelper recommendationHelper) {
        this.collectionsService = collectionsService;
        this.fragranceService = fragranceService;
        this.recommendationHelper = recommendationHelper;
    }

    @Override
    public BaseResponse<CBFResponse> getCBFRecommendations(int limit, BaseParam baseParam) {

        var userCollections = collectionsService.getUserCollections(1, "correlationId").data();

        var userCollectionIds = userCollections.items()
                .stream().map(CollectionsResponse.FragranceDetails::fragranceId)
                .collect(Collectors.toSet());

        logger.info("Fragrance IDs for CBF: {}", userCollectionIds);

        var userCollectionNotes = fragranceService.getPerfumeNotes(new FragranceBatchNotesParam(userCollectionIds), 1, "correlationId");
        var allFragranceNotes = fragranceService.getPerfumeNotes(1, "correlationId");

        logger.info("CBF Recommendation result: {}", userCollectionNotes);

        var userFragranceVector = recommendationHelper.getCollectionVector(userCollectionNotes.data().fragranceNoteLists());
        var allFragranceVector = recommendationHelper.getAllPerfumesVector(allFragranceNotes.data());

        var result = recommendationHelper.calculateCBFRecommendations(
                userFragranceVector,
                allFragranceVector,
                limit
        );

        logger.info("CBF Recommendation final result: {}", result);

        return null;
    }

}
