package com.merufureku.aromatica.recommendation_service.helper;

import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.NoteResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.merufureku.aromatica.recommendation_service.constants.RecommendationCollectionConstants.*;

@Component
public class RecommendationHelper {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public Map<Long, Float> getCollectionVector(List<FragranceNoteListResponse.FragranceNoteList> fragranceNoteLists) {

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
            Map<Long, Float> perfumeVector = perfumeEntry.getValue();

            logger.info("Calculating similarity for perfume ID: {}, {}", perfumeId, perfumeVector);

            float similarityScore = 0.0f;

            for (Map.Entry<Long, Float> noteEntry : userCollectionVector.entrySet()) {
                Long collectionNoteId = noteEntry.getKey();
                Float collectionNoteWeight = noteEntry.getValue();

                Float perfumeNoteWeight = perfumeVector.getOrDefault(collectionNoteId, 0.0f);
                similarityScore += collectionNoteWeight * perfumeNoteWeight;
            }

            similarityScores.put(perfumeId, similarityScore);
        }

        logger.info("Calculated similarity scores for {} perfumes", similarityScores.size());
        logger.info(similarityScores);

        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Float>comparingByValue().reversed()).limit(limit)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }

    private float getNoteWeight(NoteResponse note) {
        return switch (note.type().toLowerCase()) {
            case "top" -> TOP_NOTE_SCORE;
            case "middle" -> MIDDLE_NOTE_SCORE;
            case "base" -> BASE_NOTE_SCORE;
            default -> 0.0f;
        };
    }

}
