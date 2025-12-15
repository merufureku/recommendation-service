package com.merufureku.aromatica.recommendation_service.helper;

import com.merufureku.aromatica.recommendation_service.dto.responses.CBFResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceDetailedListResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.NoteResponse;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.merufureku.aromatica.recommendation_service.constants.RecommendationCollectionConstants.*;
import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.NO_FRAGRANCE_TO_RECOMMEND;
import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.NO_USER_COLLECTION;

@Component
public class RecommendationHelper {

    public Map<Long, Float> getCollectionVector(List<FragranceNoteListResponse.FragranceNoteList> fragranceNoteLists) {

        if (fragranceNoteLists.isEmpty()) {
            throw new ServiceException(NO_USER_COLLECTION);
        }

        Map<Long, Float> noteVector = new HashMap<>();

        for (FragranceNoteListResponse.FragranceNoteList noteList: fragranceNoteLists) {
            for (NoteResponse note: noteList.noteResponseList()) {
                float noteWeight = getNoteWeight(note);
                noteVector.merge(note.id(), noteWeight, Float::sum);
            }
        }

        return noteVector;
    }

    public Map<Long, Map<Long, Float>> getAllPerfumesVector(FragranceNoteListResponse allPerfumes){

        if (allPerfumes.fragranceNoteLists().isEmpty()) {
            throw new ServiceException(NO_FRAGRANCE_TO_RECOMMEND);
        }

        Map<Long, Map<Long, Float>> allPerfumeVector = new HashMap<>();

        for (FragranceNoteListResponse.FragranceNoteList noteList: allPerfumes.fragranceNoteLists()) {

            Map<Long, Float> noteVector = new HashMap<>();

            for (NoteResponse note: noteList.noteResponseList()) {
                float noteWeight = getNoteWeight(note);
                noteVector.merge(note.id(), noteWeight, Float::sum);
            }

            allPerfumeVector.put(noteList.fragranceId(), noteVector);
        }

        return allPerfumeVector;
    }

    public Map<Long, Float> calculateCBFRecommendations(Map<Long, Float> userCollectionVector,
                                                Map<Long, Map<Long, Float>> allPerfumesVector,
                                                int limit) {

        Map<Long, Float> similarityScores = new HashMap<>();

        for (Map.Entry<Long, Map<Long, Float>> perfumeEntry : allPerfumesVector.entrySet()) {
            Long perfumeId = perfumeEntry.getKey();
            float similarityScore = getSimilarityScore(userCollectionVector, perfumeEntry);

            similarityScores.put(perfumeId, similarityScore);
        }

        return similarityScores.entrySet().stream()
                .sorted((e1, e2) -> Float.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll
                );
    }

    private static float getSimilarityScore(Map<Long, Float> userCollectionVector, Map.Entry<Long, Map<Long, Float>> perfumeEntry) {
        Map<Long, Float> perfumeVector = perfumeEntry.getValue();

        float similarityScore = 0.0f;

        for (Map.Entry<Long, Float> noteEntry : userCollectionVector.entrySet()) {
            Long collectionNoteId = noteEntry.getKey();
            Float collectionNoteWeight = noteEntry.getValue();

            Float perfumeNoteWeight = perfumeVector.getOrDefault(collectionNoteId, 0.0f);
            similarityScore += collectionNoteWeight * perfumeNoteWeight;
        }
        return similarityScore;
    }

    public List<CBFResponse.Recommendations> createCbfResponse(Map<Long, FragranceDetailedListResponse.FragranceDetailedResponse> fragranceMap,
                                         Map<Long, Float> cbfResult) {
        return cbfResult.entrySet().stream()
                .map(entry -> {

                    Long id = entry.getKey();
                    Float score = entry.getValue();

                    var fragrance = fragranceMap.get(id);

                    return new CBFResponse.Recommendations(
                            fragrance.fragranceId(),
                            fragrance.name(),
                            fragrance.brand(),
                            fragrance.description(),
                            score
                    );
                })
                .toList();
    }

    private static float getNoteWeight(NoteResponse note) {
        return switch (note.type().toLowerCase()) {
            case "top" -> TOP_NOTE_SCORE;
            case "middle" -> MIDDLE_NOTE_SCORE;
            case "base" -> BASE_NOTE_SCORE;
            default -> 0.0f;
        };
    }

}
