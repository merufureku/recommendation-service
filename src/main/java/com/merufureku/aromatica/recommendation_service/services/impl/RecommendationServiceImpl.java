package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.dto.params.BaseParam;
import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.*;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import com.merufureku.aromatica.recommendation_service.helper.AsyncExceptionMapper;
import com.merufureku.aromatica.recommendation_service.helper.RecommendationHelper;
import com.merufureku.aromatica.recommendation_service.services.async.AsyncCollectionClient;
import com.merufureku.aromatica.recommendation_service.services.async.AsyncFragranceClient;
import com.merufureku.aromatica.recommendation_service.services.async.AsyncReviewsClient;
import com.merufureku.aromatica.recommendation_service.services.async.AsyncVectorBuilder;
import com.merufureku.aromatica.recommendation_service.services.interfaces.ICollectionService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IFragranceService;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IRecommendationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.merufureku.aromatica.recommendation_service.constants.RecommendationCollectionConstants.MINIMUM_RATING;
import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.NO_FRAGRANCE_TO_RECOMMEND;

@Service
public class RecommendationServiceImpl implements IRecommendationService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final ICollectionService collectionsService;
    private final IFragranceService fragranceService;
    private final RecommendationHelper recommendationHelper;
    private final AsyncFragranceClient asyncFragranceClient;
    private final AsyncReviewsClient asyncReviewsClient;
    private final AsyncCollectionClient asyncCollectionClient;
    private final AsyncVectorBuilder asyncVectorBuilder;

    public RecommendationServiceImpl(ICollectionService collectionsService, IFragranceService fragranceService, RecommendationHelper recommendationHelper, AsyncFragranceClient asyncFragranceClient, AsyncReviewsClient asyncReviewsClient, AsyncCollectionClient asyncCollectionClient, AsyncVectorBuilder asyncVectorBuilder) {
        this.collectionsService = collectionsService;
        this.fragranceService = fragranceService;
        this.recommendationHelper = recommendationHelper;
        this.asyncFragranceClient = asyncFragranceClient;
        this.asyncReviewsClient = asyncReviewsClient;
        this.asyncCollectionClient = asyncCollectionClient;
        this.asyncVectorBuilder = asyncVectorBuilder;
    }

    @Override
    public BaseResponse<RecommendationResponse> getCBFRecommendations(Integer userId, int limit, BaseParam baseParam) {
        logger.info("Fetching CBF recommendations for user ID: {}", userId);

        var userCollections = collectionsService.getUserCollections(userId, 1, baseParam.correlationId()).data();
        if (userCollections.fragrances().isEmpty()){
            throw new ServiceException(NO_FRAGRANCE_TO_RECOMMEND);
        }

        var userCollectionIds = userCollections.fragrances()
                .stream().map(UserCollectionsResponse.FragranceDetails::fragranceId)
                .collect(Collectors.toSet());

        var userNotesFuture = asyncFragranceClient.getUserCollectionNotes(userCollectionIds, baseParam.correlationId());
        var allNotesFuture = asyncFragranceClient.getAllFragranceNotes(userCollectionIds, baseParam.correlationId());

        completeFuture(userNotesFuture, allNotesFuture);

        var userCollectionNotes = userNotesFuture.getNow(new FragranceNoteListResponse(new ArrayList<>()));
        var allFragranceNotes = allNotesFuture.getNow(new FragranceNoteListResponse(new ArrayList<>()));

        var userFragranceVectorsFuture = asyncVectorBuilder.buildUserVector(userCollectionNotes.fragranceNoteLists());
        var allFragranceVectorsFuture = asyncVectorBuilder.buildAllPerfumeVectors(allFragranceNotes);

        completeFuture(userFragranceVectorsFuture, allFragranceVectorsFuture);

        var userFragranceVector = userFragranceVectorsFuture.getNow(null);
        var allFragranceVector = allFragranceVectorsFuture.getNow(null);

        var cbfResult = recommendationHelper.calculateCBFRecommendations(userFragranceVector, allFragranceVector, limit);

        var recommendedFragrance = getRecommendedFragrance(cbfResult.keySet(), baseParam);

        var response = recommendationHelper.createCbfResponse(recommendedFragrance, cbfResult);

        logger.info("CBF recommendations for user ID: {} fetched successfully", userId);
        return new BaseResponse<>(HttpStatus.OK.value(),
                "Get CBF Recommended Perfume Success", new RecommendationResponse(response));
    }

    @Override
    public BaseResponse<RecommendationResponse> getCFRecommendations(Integer userId, int limit, BaseParam baseParam) {

        logger.info("Fetching CF recommendations for user ID: {}", userId);

        var userReviewsFuture = asyncReviewsClient.getUserReviews(userId, MINIMUM_RATING,1, baseParam.correlationId());
        var userCollectionsFuture = asyncCollectionClient.getUserCollection(userId, 1, baseParam.correlationId());
        completeFuture(userReviewsFuture, userCollectionsFuture);

        var userReviews = userReviewsFuture.getNow(null);
        var userCollections = userCollectionsFuture.getNow(null);

        // Returns Map<Long, Map<Long, Double>> where key is userId and value is map of fragranceId and interaction score
        var targetUserInteractions = recommendationHelper.targetUserInteraction(userCollections, userReviews);
        var targetUserAllPerfumes = targetUserInteractions.values().iterator().next().keySet();

        // Get all reviews with minimum rating (4) and get only the fragrances collected and reviewed by target users
        // Exclude fragrances already collected by the target user
        var allUserReviewsFuture = asyncReviewsClient.getReviews(
                userId, new GetFragranceBatchParam(targetUserAllPerfumes),
                MINIMUM_RATING, 1, baseParam.correlationId());

        // Get all collections with the fragrances interacted by the target user
        var allUsersCollectionFuture = asyncCollectionClient.getAllCollectionsFromSimilarFragrance(
                userId, new GetFragranceBatchParam(targetUserAllPerfumes),
                1, baseParam.correlationId());
        completeFuture(allUserReviewsFuture, allUsersCollectionFuture);

        var allReviews = allUserReviewsFuture.getNow(null);
        var allCollections = allUsersCollectionFuture.getNow(null);

        // Returns Map<Long, Map<Long, Double>> where key is userId and value is map of fragranceId and interaction score
        var allUserInteractions = recommendationHelper.allUserInteraction(allCollections, allReviews);

        var similarityResult = recommendationHelper.getSimilarityScore(userId, targetUserInteractions, allUserInteractions);
        var topCandidateUsers = similarityResult.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        var topCandidatePerfumes = recommendationHelper.getTopCandidatePerfumes(topCandidateUsers, targetUserAllPerfumes, allUserInteractions, similarityResult, limit);
        var recommendedFragrance = getRecommendedFragrance(topCandidatePerfumes.keySet(), baseParam);

        var response = recommendationHelper.createCfResponse(recommendedFragrance, topCandidatePerfumes);

        return new BaseResponse<>(HttpStatus.OK.value(),
                "Get CF Recommended Perfume Success", new RecommendationResponse(response));
    }

    private Map<Long, FragranceDetailedListResponse.FragranceDetailedResponse> getRecommendedFragrance(Set<Long> fragranceIds, BaseParam baseParam) {
        var recommendedFragranceMetadata = fragranceService.getPerfumes(new GetFragranceBatchParam(fragranceIds), 1, baseParam.correlationId()).data();

        return recommendedFragranceMetadata.fragrances().stream()
                .collect(Collectors.toMap(
                        FragranceDetailedListResponse.FragranceDetailedResponse::fragranceId,
                        Function.identity()
                ));
    }

    private void completeFuture(CompletableFuture<?>... futures) {
        try{
            CompletableFuture.allOf(futures).join();
        } catch (Exception e){
            logger.error("Error completing futures", e);
            throw AsyncExceptionMapper.map(e);
        }
    }
}
