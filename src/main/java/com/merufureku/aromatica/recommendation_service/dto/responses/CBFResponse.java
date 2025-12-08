package com.merufureku.aromatica.recommendation_service.dto.responses;

import java.util.List;

public record CBFResponse(Integer userId, List<Recommendations> recommendations) {

    public record Recommendations(Long fragranceId, String name, String brand, float similarityScore){}

}
