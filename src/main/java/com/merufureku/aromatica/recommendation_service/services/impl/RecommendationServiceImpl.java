package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.params.ExcludeFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.*;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import com.merufureku.aromatica.recommendation_service.helper.RecommendationHelper;
import com.merufureku.aromatica.recommendation_service.services.interfaces.ICollectionService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IFragranceService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.NO_USER_COLLECTION;

@Service
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
    public BaseResponse<CBFResponse> getCBFRecommendations(Integer userId, int limit, BaseParam baseParam) {

        var userCollections = collectionsService.getUserCollections(userId, 1, "correlationId").data();

        logger.info("User Collections for CBF: {}", userCollections);
        if (userCollections.fragrances().isEmpty()){
            throw new ServiceException(NO_USER_COLLECTION);
        }

        var userCollectionIds = userCollections.fragrances()
                .stream().map(CollectionsResponse.FragranceDetails::fragranceId)
                .collect(Collectors.toSet());

        logger.info("Fragrance IDs for CBF: {}", userCollectionIds);

        // to add async call here
        var userCollectionNotes = fragranceService.getPerfumeNotes(new GetFragranceBatchParam(userCollectionIds), 1, "correlationId");
        var allFragranceNotes = fragranceService.getPerfumeNotes(new ExcludeFragranceBatchParam(userCollectionIds), 1, "correlationId");

        logger.info("CBF Recommendation result: {}", userCollectionNotes);

        // to add async call here
        var userFragranceVector = recommendationHelper.getCollectionVector(userCollectionNotes.data().fragranceNoteLists());
        var allFragranceVector = recommendationHelper.getAllPerfumesVector(allFragranceNotes.data());

        var cbfResult = recommendationHelper.calculateCBFRecommendations(userFragranceVector, allFragranceVector, limit);

        var fragrancesMetadata = fragranceService.getPerfumes(new GetFragranceBatchParam(cbfResult.keySet()), 1, "correlationId").data();
        var fragranceMap = fragrancesMetadata.fragrances().stream()
                .collect(Collectors.toMap(
                        FragranceDetailedListResponse.FragranceDetailedResponse::fragranceId,
                        Function.identity()
                ));

        var response = recommendationHelper.createCbfResponse(fragranceMap, cbfResult);
        return new BaseResponse<>(HttpStatus.OK.value(),
                "Get Recommended Perfume Success", new CBFResponse(response));
    }
}
