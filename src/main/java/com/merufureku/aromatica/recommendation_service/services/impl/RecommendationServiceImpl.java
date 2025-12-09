package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.params.ExcludeFragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.params.FragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CBFResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CollectionsResponse;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import com.merufureku.aromatica.recommendation_service.helper.RecommendationHelper;
import com.merufureku.aromatica.recommendation_service.services.interfaces.ICollectionService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IFragranceService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
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
        var userCollectionNotes = fragranceService.getPerfumeNotes(new FragranceBatchNotesParam(userCollectionIds), 1, "correlationId");
        var allFragranceNotes = fragranceService.getPerfumeNotes(new ExcludeFragranceBatchNotesParam(userCollectionIds), 1, "correlationId");

        logger.info("CBF Recommendation result: {}", userCollectionNotes);

        // to add async call here
        var userFragranceVector = recommendationHelper.getCollectionVector(userCollectionNotes.data().fragranceNoteLists());
        var allFragranceVector = recommendationHelper.getAllPerfumesVector(allFragranceNotes.data());

        var cbfResult = recommendationHelper.calculateCBFRecommendations(
                userFragranceVector,
                allFragranceVector,
                limit
        );

        var recommendations = new ArrayList<CBFResponse.Recommendations>();

        for (Map.Entry<Long, Float> noteEntry : cbfResult.entrySet()) {

            var recommendedFragrance = fragranceService.getPerfumeById(noteEntry.getKey(), 1, "correlationId").data();

            recommendations.add(new CBFResponse.Recommendations(recommendedFragrance.id(), recommendedFragrance.name(),
                    recommendedFragrance.brand(), recommendedFragrance.description(), noteEntry.getValue()));
        }

        return new BaseResponse<>(HttpStatus.OK.value(),
                "Get Recommended Perfume Success", new CBFResponse(recommendations));
    }

}
