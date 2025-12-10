package com.merufureku.aromatica.recommendation_service.dto.responses;

import java.util.List;

public record FragranceDetailedListResponse(List<FragranceDetailedResponse> fragrances) {

    public record FragranceDetailedResponse(
            Long fragranceId,
            String name,
            String brand,
            String description,
            List<NoteResponse> noteResponse){}
}