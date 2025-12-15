package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.*;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import com.merufureku.aromatica.recommendation_service.helper.AsyncExceptionMapper;
import com.merufureku.aromatica.recommendation_service.helper.RecommendationHelper;
import com.merufureku.aromatica.recommendation_service.services.interfaces.ICollectionService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IFragranceService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.NO_USER_COLLECTION;

@Service
public class RecommendationServiceImpl implements IRecommendationService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final ICollectionService collectionsService;
    private final IFragranceService fragranceService;
    private final RecommendationHelper recommendationHelper;
    private final AsyncFragranceClient asyncFragranceClient;
    private final AsyncVectorBuilder asyncVectorBuilder;

    public RecommendationServiceImpl(ICollectionService collectionsService, IFragranceService fragranceService, RecommendationHelper recommendationHelper, AsyncFragranceClient asyncFragranceClient, AsyncVectorBuilder asyncVectorBuilder) {
        this.collectionsService = collectionsService;
        this.fragranceService = fragranceService;
        this.recommendationHelper = recommendationHelper;
        this.asyncFragranceClient = asyncFragranceClient;
        this.asyncVectorBuilder = asyncVectorBuilder;
    }

    @Override
    public BaseResponse<CBFResponse> getCBFRecommendations(Integer userId, int limit, BaseParam baseParam) {
        logger.info("Fetching CBF recommendations for user ID: {}", userId);

        var userCollections = collectionsService.getUserCollections(userId, 1, baseParam.correlationId()).data();
        if (userCollections.fragrances().isEmpty()){
            throw new ServiceException(NO_USER_COLLECTION);
        }

        var userCollectionIds = userCollections.fragrances()
                .stream().map(CollectionsResponse.FragranceDetails::fragranceId)
                .collect(Collectors.toSet());

        var userNotesFuture = asyncFragranceClient.getUserCollectionNotes(userCollectionIds, baseParam.correlationId());
        var allNotesFuture = asyncFragranceClient.getAllFragranceNotes(userCollectionIds, baseParam.correlationId());

        try{
            CompletableFuture.allOf(userNotesFuture, allNotesFuture).join();
        } catch (Exception e){
            logger.error("Error fetching fragrance notes for CBF recommendations for user ID: {}", userId, e);
            throw AsyncExceptionMapper.map(e);
        }

        var userCollectionNotes = userNotesFuture.getNow(new FragranceNoteListResponse(new ArrayList<>()));
        var allFragranceNotes = allNotesFuture.getNow(new FragranceNoteListResponse(new ArrayList<>()));
        var userFragranceVectorsFuture = asyncVectorBuilder.buildUserVector(userCollectionNotes.fragranceNoteLists());
        var allFragranceVectorsFuture = asyncVectorBuilder.buildAllPerfumeVectors(allFragranceNotes);

        try{
            CompletableFuture.allOf(userFragranceVectorsFuture, allFragranceVectorsFuture).join();
        } catch (Exception e){
            logger.error("Error building fragrance vectors for CBF recommendations for user ID: {}", userId, e);
            throw AsyncExceptionMapper.map(e);
        }
        var userFragranceVector = userFragranceVectorsFuture.getNow(null);
        var allFragranceVector = allFragranceVectorsFuture.getNow(null);

        var cbfResult = recommendationHelper.calculateCBFRecommendations(userFragranceVector, allFragranceVector, limit);

        var fragrancesMetadata = fragranceService.getPerfumes(new GetFragranceBatchParam(cbfResult.keySet()), 1, baseParam.correlationId()).data();
        var fragranceMap = fragrancesMetadata.fragrances().stream()
                .collect(Collectors.toMap(
                        FragranceDetailedListResponse.FragranceDetailedResponse::fragranceId,
                        Function.identity()
                ));

        var response = recommendationHelper.createCbfResponse(fragranceMap, cbfResult);

        logger.info("CBF recommendations for user ID: {} fetched successfully", userId);
        return new BaseResponse<>(HttpStatus.OK.value(),
                "Get Recommended Perfume Success", new CBFResponse(response));
    }
}
